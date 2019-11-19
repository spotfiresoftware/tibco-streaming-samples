;(function(angular){
	'use strict';
	angular
		.module('com.tibco.sb.ldm.web.plugins.sample')
		.directive('lvSampleVis', lvSampleVis);

	lvSampleVis.$inject = ['$log', 'FormatterService', 'VisualizationContract'];

	function lvSampleVis($log, FormatterService, VisualizationContract){

		var logger = $log.getInstance('/com.tibco.sb.ldm/web/plugins/sample/lvSampleVis');

		LvSampleVisCtrl.$inject = ['$scope'];
		return {
			restrict: 'A',
			templateUrl: 'plugins/com.tibco.sb.ldm.web.plugins.sample/sampleVis.tpl.html',
			scope: false,
			controller: LvSampleVisCtrl,
			controllerAs: 'vm', 			//the controller instance will be referred to as "vm" in the sampleVis.tpl.html file
			bindToController: true			//scope values will be bound to the controller instance
		};

		function LvSampleVisCtrl($scope){

			//The directive's scope will have the following properties bound to it
			// contract 	 - 	'contract' is an object which facilitate two-way communication between LiveView Web
			// 					and the visualization.
			// visualization - 	this is the config object which the visualization editor modifies during editing phase.
			// 					The structure of this object is designed by the visualization plugin developer.
			// 					It should contain all the information the plugin developer needs to render the
			// 					visualization including which data sources (identified by their IDs) to render and how
			//					to render them.
			// rootElement	-	The container element for the visualization. A reference to this element is handy when
			//					one needs to update visualization properties (e.g. width and height) according to its
			//					containing element.

			var dataUnsubscriberFunc,	//The function which is to be called to unsubscribe a data subscription
				currentTupleId,			//The ID of the tuple whose data is being displayed in the visualization
				vm = this;				//Maintain a pointer to the controller instance, named after the "controllerAs" attribute of the directive

			$scope.$on('$destroy', handleDirectiveDestroy);

			vm.fields = {};		//The field objects to display in the visualization. Each object will have properties:
								// name - the name of the field
								// type - the LiveView data type of the field
								//
								// as well as optional fields:
								//    value - the field value (unset means the display will be blank for that field)
								//    formatter - function that will be used to format the value if it is of type double
								//    hide - flag indicating whether or not to display the field based on what was
								//       configured in visualization config editor

			//The contract property bound to the directive's scope (see above) contains the following properties
			//which the visualization needs to assign to an appropriate callback function. Doing so allows LiveView Web
			//to communicate various events to the visualization and gives the visualization a way to notify LiveView
			//Web when events have occurred inside the visualization. A visualization developer need not assign callback
			//functions to all of these properties; it is acceptable to just specify those which the visualization needs.

			//Generally speaking, if a property begins with "on" (e.g. onData or onSchemaSet) it refers to a function
			//that LiveView Web will invoke to communicate events to the visualization. Properties that begin with
			//"handle" refer to functions that a visualization can invoke to communicate information back to LiveView
			//Web.

			// onConfigChanged		-	Called by LiveView Web when the visualization's configuration has changed. This
			// 							typically occurs when users change settings in the visualization configuration
			//							editor, but it may also be triggered by other LiveView Web internal mechanisms
			//							(like card decorators). No arguments are passed to this function because the
			//							configuration object is already accessible via the visualization property that's
			//							bound to the controller instance (i.e. vm.visualization.config).

			// onDataSourceChange	- 	Called by LiveView Web when the data configuration has changed. This typically
			// 							occurs when users change settings in the data configuration tab (e.g. editing
			//							a LiveView query), but it may also be triggered by other LiveView Web internal
			//							mechanisms (like card decorators).
			//							Arguments:
			//								addedDataIds		-	The ids of any added data sources
			// 								updatedDataIds		-	The ids of any updated data sources
			// 								deletedDataIds		-	The ids of any deleted data sources

			// onSchemaSet			- 	Called by LiveView Web to provide the schema of the data source to the
			//							visualization.
			//							Arguments:
			//								dataSourceId			-	The id of the data source which the schema defines
			// 								queryEventData
			//										schema			-	The schema of the data source
			//										*other fields in queryEventData should be ignored

			// onData				- 	Called by LiveView Web to provide data events for the subscribed data source.
			//							This callback is invoked whenever an add, update, or delete event is received
			//							for the data source if buffering is not enabled. If buffering is enabled (and it
			//							is by default), then this function is called when the buffer gets flushed.
			//							Arguments
			//								dataSourceId			- 	The id of the data source whose data changed
			// 								dataStore				-   Reference to coalesced data for the data source
			//								dataEvents	 			-	The data events. Each event has the following
			//															properties:
			//															type - The type of the event. Can be one of:
			//																VisualizationContract.DataEvent.Types.ADD
			//																VisualizationContract.DataEvent.Types.UPDATE
			//																VisualizationContract.DataEvent.Types.DELETE
			//															data - The data payload of the event. For
			//																LiveView data sources, this is the tuple
			//																that was affected by the add/update/remove
			//															affectedPositions - An object containing two
			// 																properties: oldIndex and newIndex. This info
			//																can be used to correlate positions between
			//																the data that's in the dataStore and data
			//																that's in your visualization model. On add
			//																events, newIndex is the position where the
			//																data was inserted and oldIndex will be -1.
			// 																On update events, oldIndex will be the
			// 																position of the data before the update was
			// 																applied and newIndex will be the position of
			// 																the data after the update was applied. On
			//																delete events, newIndex will be -1 and
			//																oldIndex will be the position of the data
			//																before it was removed. This information is
			//																critical for those visualizations that want
			//																to ensure consistency with ordered data
			//																sources (e.g. those resulting from LiveView
			//																queries with an ORDER BY clause).

			// onError				- 	Called by LiveView Web when an error occurs in the data source (e.g. a query
			// 							error for a LiveView query). LiveView provides a default implementation which
			//							shows a dialog. Plugin developers can override that behavior by providing their
			//							own implementation by setting the contract.onError to a function of their
			//							choosing.
			//							Arguments
			//								dataSourceId			-	The id of the data source that produced the error
			// 								errorMsg				-	The error message

			// subscribeToData		- 	Subscribes a visualization to the data produced by the specified data source.
			//							The function should be invoked passing the data source ID that the visualization
			//							wants to subscribe to. The function should be invoked when the visualization is
			//							ready to receive data-related events (via the contract-defined callback
			//							functions). The function returns a function that the visualization can invoke to
			//							unsubscribe from the data source.
			//
			// 							By default, data events are buffered by LiveView Web and passed to visualizations
			// 							via the contract.onData callback every 500ms. The contract.onData function is
			//							passed the data source ID of the data source that updated, a reference to a
			//							coalesced view of the data source's data, and an array of the events that
			//							occurred since the last call to onDataSet. Buffering can be switched off by
			//							setting disableBuffering to true in the optional settings object passed when
			// 							invoking subscribeToData. For performance reasons, it is highly recommended that
			//							you do not disable buffering.
			//							Arguments
			//								dataSourceId			-	The id of the data source to subscribe to
			//								settings				-  	Additional optional settings

			// handleVizError		- 	If an error occurs in a visualization and that visualization wants to notify the
			//							user that the error occurred in a LiveView Web -like manner, the visualization
			//							should call handleVizError to delegate error handling to LiveView Web.
			//							Arguments
			//								error					- 	The error, can be string or Error object
			//								show					-	Flag indicating whether or not to show the error
			//															to the user (true will show the user, false by
			//															default)
			//								context					-	Object which is logged as contextual information

			// handleDataSelected	-	In order to enable functionality like card links, visualizations need a
			//							mechanism to notify LiveView Web when a data point or data marker has been
			//							selected within the visualization. The handleDataSelected function provides this
			//							mechanism.
			//							Arguments:
			//								dataSourceId			-	The id of the data source driving the selected
			// 															data point or marker
			//								tuple					-	The tuple or row associated with the selected
			//															data point or marker
			//								categoryField			-	An optional field which represents the category
			// 															axis value in the tuple/row
			//								valueField				-	An optional field which represent the value axis
			// 															value in the tuple

			// showDataSensitiveMenu  -	Delegates the display of a contextual menu to LiveView Web.
			//							Arguments:
			//								x						- The x-coordinate of the contextual mouse click
			//								y						- The y-coordinate of the contextual mouse click
			//								visualizationsMarkers	- Array of VisualizationMarker objects where a
			//														VisualizationMarker consists of name, dataSourceId,
			//														schema, tuples, and context.
			//

			// handleConfigChanged	  -	If a visualization is interactive, allowing users to alter visualization
			//							configuration by interacting with the visualization (e.g. resizing grid columns
			//							or drawing routes on a map), handleConfigChanged provides a mechanism for the
			//							visualization to communicate configuration changes based on those user
			//							interactions. This function takes no arguments as it's invocation just acts as
			//							a signal to LiveView Web that configuration data should be saved for the
			//							visualization.
			//


			//map callback functions to the appropriate contract properties
			$scope.contract.onConfigChanged = onConfigChanged;
			$scope.contract.onDataSourceChange = onDataSourceChange;
			$scope.contract.onSchemaSet = onSchemaSet;
			$scope.contract.onData = onData;

			//subscribe to data with default settings (IMPORTANT - do this _after_ configuring contract callbacks)
			dataUnsubscriberFunc = $scope.contract.subscribeToData($scope.visualization.config.dataSourceId);

			function onConfigChanged(){
				//Reprocess the $scope.visualization.config object to make changes in the visualization rendering.

				//Since our sample visualization editor is watching for changes to the query schema and updating the
				// visualization configuration when the schema changes, saving the card after only modifying the
				// query may (in addition to an onDataSourceChange call) also result in a call to onConfigChanged.
				// Usually, onConfigChanged is only invoked when the user clicks save if there were changes made to the
				// visualization configuration via the visualization configuration editor. Because visualization
				// handling of a new or modified schema does not occur until onSchemaSet, the  visualization has not yet
				// processed the change in schema and thus may not know about some of the new visualization
				// configuration settings.

				// In this sample, we do not yet know whether fields have been added or removed from the set of
				// available fields in our view model. So, we only modify our displayed fields if they are also in the
				// visualization configuration's set of available fields. Fields not yet being displayed will be added
				// later when the onSchemaSet callback is invoked. Displayed fields no longer in the schema are also
				// purged at that time.

				angular.forEach($scope.visualization.config.availableFields, function(fieldConfig, fieldName){
					if(vm.fields[fieldName]){
						vm.fields[fieldName].hide = !fieldConfig.enabled;
					}
				});
			}

			/**
			 * @param {Array} addedDataIds all data sources that were added during the change
			 * @param {Array} updatedDataIds all data sources that were updated during the change
			 * @param {Array} deletedDataIds all data sources that were removed during the change
			 */
			function onDataSourceChange(addedDataIds, updatedDataIds, deletedDataIds){
				//Here is where you could handle any changes the user makes in the data configuration for the card
				// containing this visualization. This function is invoked when the user saves the card and there are
				// changes in the data configuration. It will occur before the request is made to the back-end to update
				// the data configuration (e.g. the LiveView query). In our sample, we only care about changes in the
				// data schema, so we defer handling to the onSchemaSet function which occurs after the back-end has
				// updated its configuration (e.g. LiveView has applied changes to the configured query).

				//The arguments passed to this function can be safely ignored for now. They're provided so that if/when
				// LiveView Web makes multiple data sources available to a card (e.g. multiple LiveView queries), the
				// visualization can access those data sources for display.
			}

			/**
			 * @param {String} dataSourceId The ID of the data source whose schema was set
			 * @param {Object} queryEventData An object containing information about the set schema event including a
			 *  schema property with the LiveView.Schema object for the data source
			 */
			function onSchemaSet(dataSourceId, queryEventData){
				//Here we handle the event that tells us that the data source driving this visualization has started,
				// and that its data schema has been received.
				var fieldConfigs = $scope.visualization.config.availableFields; //shorthand reference

				//clear out stale field configs
				Object.keys(vm.fields).forEach(function(vmFieldName){
					if(!queryEventData.schema.fieldsMap.hasOwnProperty(vmFieldName)){
						delete vm.fields[vmFieldName];
					}
				});

				//Build/re-build the view model fields using the available schema fields. For those fields of type
				// double or timestamp, we create a formatter function that will format the value for better readability
				angular.forEach(queryEventData.schema.fields, function(schemaField){
					var vmField = vm.fields[schemaField.name] || //use the exiting field if it exists, else make one
						{
							name: schemaField.name,
							type: schemaField.type,
							hide: fieldConfigs[schemaField.name] ? fieldConfigs[schemaField.name].enabled !== true : false
						};
					if(schemaField.type === 'double'){
						vmField.formatter = FormatterService.compile('${'+vmField.name+'|number:2}', queryEventData.schema);
					}
					else if(schemaField.type === 'timestamp'){
						vmField.formatter = FormatterService.compile('${'+vmField.name+'|date:HH:mm:ss:sss}', queryEventData.schema);
					}
					vm.fields[vmField.name] = vmField;
				});
			}

			/**
			 * @param {String} dataSourceId The ID of the data source whose data was updated
			 * @param {Event} dataStore A reference to the whole coalesced data set for the data source
			 * @param {Array<VisualizationContract.DataEvent>} dataEvents The data events that occurred since the last
			 * LiveView Web call to contract.onData
			 */
			function onData(dataSourceId, dataStore, dataEvents){
				var i;

				//Here we have a choice about how we want to handle the data coming from the data source. Since data
				//events are buffered by LiveView Web, there may be many data events in the dataEvents array. Depending
				//on how many events there are or what exactly our visualization is showing, we may want to forgo trying
				//to render each event individually and use the coalesced data in the dataStore to do a full, one-time,
				//redraw. This decision needs to be made individually for each visualization according to the
				//performance of its rendering routine.

				//Since this sample only shows the most recent tuple data, it would make more sense to use the dataStore
				//to set the values in the visualization instead of replaying all of the events in dataEvents. We replay
				//them instead as a means of demonstration. If we were being efficient, we would instead just set all of
				//the field values in the vm.fields map using the corresponding data in the dataStore.
				for(i = 0; i < dataEvents.length; i++){
					if(dataEvents[i].type === VisualizationContract.DataEvent.Types.ADD){
						handleNewData(dataEvents[i].data);
					}
					else if(dataEvents[i].type === VisualizationContract.DataEvent.Types.UPDATE){
						//On updates, we don't want to waste resources by updating unchanged values. The oldValues
						// property of the EventData's affectedPositions object is a fieldName->value map of only those
						// fields whose value changed.
						handleUpdateData(dataEvents[i].data, dataEvents[i].affectedPositions.oldValues);
					}
					else if(dataEvents[i].type === VisualizationContract.DataEvent.Types.DELETE){
						handleDeletedData(dataEvents[i].data);
					}
				}
			}

			function handleNewData(newData){
				//Maintain currentTupleId so we'll know whether to clear all fields during handleDeletedData
				currentTupleId = newData.id;
				//A new tuple should replace all displayed field values with those defined in the new tuple
				angular.forEach(newData.fieldMap, function(fieldValue, fieldName){
					vm.fields[fieldName].value = vm.fields[fieldName].formatter ?
						vm.fields[fieldName].formatter(newData): //the formatter requires the full tuple data
						fieldValue;
				});
			}

			function handleUpdateData(updateData, oldValues){
				//When a tuple is updated, we only replace the values that changed
				angular.forEach(oldValues, function(oldValue, fieldName){
					vm.fields[fieldName].value = vm.fields[fieldName].formatter ?
						vm.fields[fieldName].formatter(updateData): //the formatter requires the full tuple data
						updateData[fieldName];
				});
			}

			function handleDeletedData(deleteData){
				//If the tuple being displayed is the tuple that was deleted, clear the displayed field values
				if(deleteData.id === currentTupleId){
					angular.forEach(vm.fields, function(field){ delete field.value; });
					currentTupleId = -1;
				}
			}

			function handleDirectiveDestroy(event){
				//do visualization clean up and unsubscribe from all subscribed data sources
				dataUnsubscriberFunc();
			}

		}

	}

})(angular);
