'use strict';

/* Controllers */
  // signin controller
app.controller('SigninFormController',
               ['$scope', '$http', '$state', '$rootScope', 'AUTH_EVENTS', 'authService',
               function($scope, $http, $state, $rootScope, AUTH_EVENTS, authService) {

    $scope.credentials = {
        username: '',
        password: ''
    };

    $scope.login = function() {
        authService.authenticate($scope.credentials, function(authenticated) {
            if (authenticated) {
                console.log("Login succeeded")
                $scope.authError = false;
                $state.go('app.home');
            } else {
                console.log("Login failed")
                $scope.authError = true;
            }
        })
    };
  }])
;