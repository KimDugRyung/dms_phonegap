var DMSApp = ClassInternal.extend({
    _class: "DMSApp",
    init: function(self, localizationMessages) {
    	self.requestDestination = "http://drmawsservice.elasticbeanstalk.com/servicestack/mobile?format=json";
    	
        self.localizationMessages = localizationMessages;
   
        self.elements = {
            chart_holder: $('#dimensions_chart_holder'),
            yes_button: $('#s1_nbox_yes').parent(),
            no_button: $('#s1_nbox_no').parent(),
            close_button: $('#s1_nbox_close').parent(),
            step1: $('#step1'),
            step2: $('#step2'),
            countdown: $(".countdown")
        }
        self.cordovaPushInit();
        
        self.localization();
      
        self.timeout = null;// correct way
        self.dimensionsChart = null;
        
        //demo way
        self.testDataResultDestination = "http://drmawsservice.elasticbeanstalk.com/servicestack/mobile?format=json&desiredAction=result";
        
        self.eventPageNow = false; //we cannot update countdown and save performance
        self.interval = window.setInterval(function() { self.sendRequest(self.requestDestination + "&dummy=" + (new Date()).getTime(), demoPullCallback); }, 5000);
    },
    localization: function(self) {
        for(id in self.localizationMessages.ids) {
            $("#" + id).text(self.localizationMessages.ids[id]);
        }
    },
    showWelcomePage: function(self) {
    	self.eventPageNow = false;
    	self.elements.step1.hide();
    	self.elements.step2.hide();
    },
    showForecastPage: function(self, input_data) {
    	self.eventPageNow  = false;
    	self.elements.step2.hide();
    	self.elements.step1.show();
    	
		self.createDimensionsChart(self.validationChartData(input_data));  	
		
    	$("s1_nbox_text").text(self.localizationMessages.ids.s1_nbox_text);
    },
    showEventPage: function(self, duration) {
    	self.eventPageNow = true;
      	self.acceptAction(duration);
    	//show correct message
    },
    showResultPage: function(self, input_data) {
    	self.eventPageNow = false;
    	self.endOfTime(self.validationChartData(input_data));
    	$("#s1_nbox_text").text(self.localizationMessages.result_message);
    },
    validationChartData: function(self, input_data) {
    	if(input_data == null || typeof input_data.sessionId == 'undefined' || input_data.sessionId == null) {
            alert(self.localizationMessages.empty_notification.replace("#variable#", "data or session_id"));
            return {x: {series: [], axisLabel: ""}, y: {series: [], axisLabel: ""}};
        }
        var xAxisData = input_data.demandHours;
        xAxisData.axisLabel = input_data.dimensions.xLabel;
        
        var yAxisData = { 
            series: [input_data.demandForecast, input_data.maximumDemand],
            axisLabel : input_data.dimensions.yLabel
        };
        return {x: xAxisData, y: yAxisData};
    },
    sendRequest: function(self, destination, callback) {
   		$.support.cors = true;
   		$.ajax({type:"GET", url:destination}).done(function(data) {
            callback(self, data);
        });
    },
 	//---------used correct way functions------------
 	initCountDown: function(self, startTime, callback) {
    	self.elements.countdown.countdown({
                        stepTime: 60,
                        format: 'mm:ss',
                        startTime: startTime,
                        timerEnd: function() { callback(); },
                        image: "images/digits.png"
        });
    },
    createDimensionsChart: function(self, chartData) {
    	var xAxisData = chartData.x;
    	var yAxisData = chartData.y;
        self.dimensionsChart = $.jqplot("dimensions_chart_holder", [yAxisData.series[0].values, yAxisData.series[1].values], 
            {
                seriesColors: [ '#4572a7', '#aa4643'],
                stackSeries: false,
                title: self.localizationMessages.chart_title,
                series:[{renderer:$.jqplot.BarRenderer}],
                axesDefaults: {
                    axes: {
                        xaxis: {
                            renderer: $.jqplot.CategoryAxisRenderer
                        },
                        x2axis: {
                            renderer: $.jqplot.CategoryAxisRenderer
                        }
                    }
                },
                legend: {
                    show: true,
                    location: 'nw',
                    xoffset: 50,
                    yoffset: 50
                }
            }
        );
    },
    acceptAction: function(self, duration) {
    	//window.clearTimeout(self.timeout);
    
    	self.elements.step1.hide();
    	self.elements.step2.show();
    	
    	if(self.dimensionsChart != null) self.dimensionsChart.destroy();
    	
    	var durationTime = parseInt(duration);
    	if(durationTime < 10) {
    		durationTime = "0" + durationTime + ":00";
    	} else {
    		durationTime = durationTime + ":00";
    	}
    	
    	self.initCountDown(durationTime, function() { self.sendRequest(self.testDataResultDestination, demoPullCallback); });
    },
    endOfTime: function(self, chartSeries) {
    	self.elements.step2.hide();
    	
    	/*
    	self.elements.yes_button.toggle();
    	self.elements.no_button.toggle();
    	self.elements.close_button.toggle();
    	*/
    	
    	self.elements.step1.show();
    		
		if(self.dimensionsChart != null) { self.dimensionsChart.destroy(); }
    	self.createDimensionsChart(chartSeries);
    },
    
    //---------unused correct way functions------------
    bindHandlers: function(self, input_data) {
    	var duration = input_data.forecastPeriod.duration;
        self.elements.yes_button.bind('click', function() { self.acceptAction(duration); });
        self.elements.no_button.bind('click', function() { self.rejectAction(); });
        var time_to_event = new Date(input_data.forecastPeriod.startDate)  - new Date();
	    self.timeout - window.createTimeout(function() {self.acceptAction(duration); }, time_to_event);
    },
    rejectAction: function(self) {
		window.sclearTimeout(self.timeout);
		//close application
    },
    cordovaPushInit: function(self) {
    	var cordovaRef = window.PhoneGap || window.Cordova || window.cordova;
        if(typeof cordovaRef != 'undefined') {
        	cordovaRef.exec(succesCallback, failCallback, "PushPlugin", "registerPush", ["304113618910"]);
        }
        function succesCallback(data) { 
        	var deviceKey = data;
        	cordovaRef.exec(void, void, "PushPlugin", "sendDeviceId", [deviceKey]);
        }
        function failCallback(data) { alert(data); }
    }
});
		
		
