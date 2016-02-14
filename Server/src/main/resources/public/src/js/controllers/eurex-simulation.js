'use strict';

/* Controllers */
app.controller('ModalInstanceCtrl', ['$rootScope', '$scope', '$modalInstance', '$http', 'items',
                function($rootScope, $scope, $modalInstance, $http, items) {

    $scope.portfolio = {};

    $scope.ok = function () {

        angular.extend($scope.portfolio, {ownerId: $rootScope.principal.username});
        console.log("Create new portfolio : " + $scope.portfolio);
        console.log($scope.portfolio);
        console.log($rootScope);

        // Create Portfolio
        $http.post('/api/portfolio/add', $scope.portfolio)
        .then(
            function(response) {
                //Success Callback
                $modalInstance.close();
            }, function(response) {
                //Failure Callback
                $scope.portfolioCreationError = 'Unable to create portfolio for reason : ';
            });
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  }])
  ; 
app.controller('ModalPortfolioCreationCtrl', ['$scope', '$modal', '$log', function($scope, $modal, $log) {
    
	$scope.items = ['item1', 'item2', 'item3'];
    
	$scope.open = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'myPortfolioCreationModalContent.html',
        controller: 'ModalInstanceCtrl',
        size: size,
        resolve: {
          items: function () {
            return $scope.items;
          }
        }
      });

      modalInstance.result.then(function (selectedItem) {
        $scope.selected = selectedItem;
      }, function () {
        $log.info('Modal dismissed at: ' + new Date());
      });
    };
  }])
  ; 
  
app.controller('DatepickerDemoCtrl', ['$scope', function($scope) {
    $scope.today = function() {
      $scope.dt = new Date();
    };
    $scope.today();

    $scope.clear = function () {
      $scope.dt = null;
    };

    // Disable weekend selection
    $scope.disabled = function(date, mode) {
      return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
    };

    $scope.toggleMin = function() {
      $scope.minDate = $scope.minDate ? null : new Date();
    };
    $scope.toggleMin();

    $scope.open = function($event) {
      $event.preventDefault();
      $event.stopPropagation();

      $scope.opened = true;
    };

    $scope.dateOptions = {
      formatYear: 'yy',
      startingDay: 1,
      class: 'datepicker'
    };

    $scope.initDate = new Date('2016-15-20');
    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[0];
  }])
  ; 
  
app.controller('EurexSimulationCtrl', ['authService', '$scope', '$filter', '$http', 'editableOptions', 'editableThemes',
  function(authService, $scope, $filter, $http, editableOptions, editableThemes){
    editableThemes.bs3.inputClass = 'input-sm';
    editableThemes.bs3.buttonsClass = 'btn-sm';
    editableOptions.theme = 'bs3';

    $scope.d1_1 = [ [0, 50] ];
    $scope.d1_2 = [ [0, 50] ];
    $scope.d1_3 = [ [0, 50] ];


	//init
	$scope.portfolios=[];
	$scope.portfolioSelected = {};

	// Event Management
    $scope.loadPosition = function(){
		$http.get("/api/positions/"+$scope.portfolioSelected._id)
            .success(function(data) {
                $scope.positions=data;
            });
    };

	$scope.$watch(function() {
        return $scope.portfolioSelected && $scope.portfolioSelected._id;
    }, function(value, last) {
        if (value) {
            $scope.loadPosition();
        } else {
            $scope.positions=[];
        }
    });

    //Init
    $http.get("/api/portfolio/list/"+authService.getPrincipal().username)
    //$http.get("/src/data/portfolios.json")
        .success(function(data) {
            $scope.portfolios=data;
            // Set the default portfolio
            $scope.portfolioSelected=$scope.portfolios[0];
        });
		
		
	$scope.computeMargin = function(){
        $http.get("/ComputeEtdMargin/"+$scope.portfolioSelected._id)
            .success(function(data) {
                //console.log(data)
                //$scope.marginResult=data;
            });
    };
}]);
