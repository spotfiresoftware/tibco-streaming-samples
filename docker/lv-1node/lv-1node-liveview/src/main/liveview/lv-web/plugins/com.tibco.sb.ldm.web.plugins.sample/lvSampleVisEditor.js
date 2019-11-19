;(function(angular){
	'use strict';
	angular
		.module('com.tibco.sb.ldm.web.plugins.sample')
		.directive('lvSampleVisEditor', lvSampleVisEditor);

	function lvSampleVisEditor(){

		LvSampleVisEditorCtrl.$inject = ['$scope'];

		return {
			restrict: 'A',
			templateUrl: 'plugins/com.tibco.sb.ldm.web.plugins.sample/sampleVisEditor.tpl.html',
			scope: {
				model: '='
			},
			controller: LvSampleVisEditorCtrl,
			controllerAs: 'vm',
			bindToController: true
		};

		function LvSampleVisEditorCtrl($scope){
			var visConfig,		//a shorthand reference to vm.model.visualization.config
				vm = this;		//consistent reference to the controller instance (aka view model)

			visConfig = vm.model.visualization.config;

			//Ensure the configuration model has the field(s) we care about. Default fields and their values should be
			// set in the "defaultConfig" property of the VisualizationPlugin constructor's "options" parameter.
			// (see file: com.tibco.sb.ldm.web.plugins.sample.js)
			if(!angular.isObject(visConfig.availableFields)){
				visConfig.availableFields = {};
			}
			//If the fields we care about exist and this editor is being loaded for...
			// a new card
			//   then configuration is a copy of the defaultConfig provided when constructing the VisualizationPlugin
			// an existing card
			//   then configuration is being loaded from the stored card configuration in LiveView Web

			//Watch for schema changes that occur when the user changes the data configuration in the Data tab of the
			// card editor. This is needed if your visualization editor enumerates available fields (e.g. to allow
			// selection of a field for a particular configuration setting like we do in this sample). Note: we pass the
			// third argument true to indicate that angular should deep-watch the object
			$scope.$watch(function(){ return vm.model.schemas; }, handleSchemasChanged, true);

			function handleSchemasChanged(newVal, oldVal){
				var schema; //shorthand reference to vm.model.schemas[visConfig.dataSourceId]

				//Note: The schemas in our model are indexed by data source ID, in the current version of LiveView Web,
				// however, there will be only one.
				if(vm.model.schemas[visConfig.dataSourceId]){
					schema = vm.model.schemas[visConfig.dataSourceId];

					//add new fields to our list of available fields in the visualization config
					angular.forEach(schema.fieldsMap, function(schemaField, fieldName){
						if(!visConfig.availableFields.hasOwnProperty(fieldName)){
							//make new schema fields enabled by default
							visConfig.availableFields[fieldName] = {enabled: true};
						}
					});

					//clean stale fields in the list of available fields
					Object.keys(visConfig.availableFields).forEach(function(availableFieldName){
						if(angular.isUndefined(schema.fieldsMap[availableFieldName])){
							delete visConfig.availableFields[availableFieldName];
						}
					});
				}

			}
		}
	}

})(angular);
