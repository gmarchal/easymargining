'use strict';

// signup controller
app.controller('SignupFormController', ['$scope', '$http', '$state', function($scope, $http, $state) {

    $scope.user = {};
    $scope.authError = null;
    $scope.signup = function() {

        $scope.authError = null;
        // Try to create
        $http.post(
            '/api/users/signup', $scope.user )
        .then(
            function(response) {
                //Success Callback
                if ( !response.data.user ) {
                  $scope.authError = response;
                }else{
                    console.log("Signup with success : go app.home")
                  $state.go('access.signin');
                }
            }, function(x) {
                //Failure Callback
                $scope.authError = 'Server Error';
            });
    };
  }])
 ;