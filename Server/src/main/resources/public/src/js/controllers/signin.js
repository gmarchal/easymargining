'use strict';

/* Controllers */
  // signin controller
app.controller('SigninFormController',
               ['$scope', '$rootScope', '$state', 'AUTH_EVENTS', 'authService',
               function($scope, $rootScope, $state, AUTH_EVENTS, authService) {

    $scope.credentials = {
        username: '',
        password: ''
    };

    $scope.login = function() {
        authService.authenticate($scope.credentials, function(data) {
            if (data.authenticated) {
                console.log("Login succeeded")
                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                $scope.setCurrentUser(data.principal);
                $scope.authError = false;
                $state.go('app.home');
            } else {
                console.log("Login failed")
                $scope.authError = true;
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            }
        })
    };
  }])
;