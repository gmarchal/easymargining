'use strict';

/* Controllers */
app
  // Flot Chart controller
  .controller('AdminCtrl', ['$scope', '$http', function($scope, $http) {

    // Load Eurex Product Definitions
    $scope.loadEurexProductDefinitions = function(){
        $http.post("/api/product/loadProductDefs")
            .success(function(data) {

            })
            .error(function(data, status) {
                console.error('Error', status, data);
            });
    };

    // Load Eurex Product Definitions
    $scope.loadEurexProducts = function(){
        $http.post("/api/product/loadProducts")
            .success(function(data) {

            })
            .error(function(data, status) {
                console.error('Error', status, data);
            });
    };
  }]);