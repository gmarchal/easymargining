'use strict';

/* Controllers */
app.controller('ModalInstanceCtrl', ['$scope', '$modalInstance', '$http', 'currentUser', 'portfolios',
                function($scope, $modalInstance, $http, currentUser, portfolios) {

    $scope.portfolio = {};

    $scope.ok = function () {

        angular.extend($scope.portfolio, {ownerId: currentUser.userId});
        console.log("Create new portfolio : " + $scope.portfolio);
        console.log($scope.portfolio);

        // Create Portfolio
        $http.post('/api/portfolio/add', $scope.portfolio)
        .then(
            function(response) {
                //Success Callback
                portfolios.unshift($scope.portfolio);
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
    
	$scope.open = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'myPortfolioCreationModalContent.html',
        controller: 'ModalInstanceCtrl',
        size: size,
        resolve: {
          currentUser: function () {
            return $scope.currentUser;
          },
          portfolios: function () {
            return $scope.portfolios;
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
  
app.controller('EurexSimulationCtrl', ['authService', '$scope', '$filter', '$http', 'PositionDataFactory',
  function(authService, $scope, $filter, $http, PositionDataFactory ){

    $scope.d1_1 = [ [0, 50] ];
    $scope.d1_2 = [ [0, 50] ];
    $scope.d1_3 = [ [0, 50] ];


	//init
	$scope.portfolios=[];
	$scope.portfolioSelected = {};
	$scope.marginError = false;
	$scope.marginErrorMessage = "";
	$scope.marginResult = {};

	// Event Management
    $scope.loadPosition = function(){
        PositionDataFactory.positions = [];
		$http.get("/api/positions/"+$scope.portfolioSelected._id)
            .success(function(data) {
                PositionDataFactory.positions = data;
            });
    };

	$scope.$watch(function() {
        return $scope.portfolioSelected && $scope.portfolioSelected._id;
    }, function(value, last) {
        if (value) {
            PositionDataFactory.portfolioId = $scope.portfolioSelected._id
            $scope.loadPosition();
        } else {
            PositionDataFactory.positions = [];
        }
    });

    //Init
    $http.get("/api/portfolio/list/"+$scope.currentUser.userId)
    //$http.get("/src/data/portfolios.json")
        .success(function(data) {
            $scope.portfolios=data;
            // Set the default portfolio
            $scope.portfolioSelected = $scope.portfolios[0];
            PositionDataFactory.portfolioId = $scope.portfolioSelected._id;
            console.log(PositionDataFactory.portfolioId);
        });
		
		
	$scope.computeMargin = function(){
        $http.get("/api/margin/computeEtd/"+$scope.portfolioSelected._id)
            .success(function(data) {
                console.log(data)
                $scope.marginResult = data;
                $scope.marginError = false;
                $scope.marginErrorMessage = "";
            })
            .error(function(data, status) {
                console.error('Error', status, data);
                $scope.marginResult = {
                    imResult: 0,
                    histoVarResult: 0,
                };
                $scope.marginError = true;
                $scope.marginErrorMessage = data.message;
            });
    };
}]);
