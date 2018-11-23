;(function(angular){
	'use strict';

	angular
		.module('com.tibco.sb.ldm.web.plugins.sample', ['com.tibco.sb.ldm'])
		.run(onModuleRun);

	onModuleRun.$inject = ['PluginRegistry'];
	function onModuleRun(PluginRegistry){
		PluginRegistry.register(
			new PluginRegistry.VisualizationPlugin('sample', 'lv-sample-vis', 'lv-sample-vis-editor', {
				name: 'Sample Vis', //Limit length to 12 characters
				defaultConfig: {
					dataSourceId: '%datasetId%',
					availableFields: {}
				},
				previewIconUrl: 'assets/img/button/vis-type/grid_60x60.png' //TODO: change this to an icon in your plug-in
			})
		);
	}

})(angular);
