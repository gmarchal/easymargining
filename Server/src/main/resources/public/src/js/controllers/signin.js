'use strict';

/* Controllers */
  // signin controller
app.controller('SigninFormController',
               ['$scope', '$http', '$state', '$rootScope', 'authService',
               function($scope, $http, $state, $rootScope, authService) {

    var authenticate = function(credentials, callback) {

        var headers = credentials ? {
            authorization : "Basic "
                    + btoa(credentials.username + ":"
                            + credentials.password)
        } : {};

        $http.get('/api/users/current', {
            headers : headers
        }).success(function(data) {
            if (data.name) {
                console.log(data);
                $rootScope.authenticated = true;
                $rootScope.principal = data.principal;
                authService.setPrincipal(data.principal);
                authService.setAuthenticated(true);
            } else {
                $rootScope.authenticated = false;
                authService.setAuthenticated(false);
            }
            callback && callback($rootScope.authenticated);
        }).error(function() {
            $rootScope.authenticated = false;
            authService.setAuthenticated(false);
            callback && callback(false);
        });

    }

    authenticate();

    $scope.credentials = {};
    $scope.login = function() {
        authenticate($scope.credentials, function(authenticated) {
            if (authenticated) {
                console.log("Login succeeded")
                $scope.authError = false;
                $rootScope.authenticated = true;
                authService.setAuthenticated(true);
                $state.go('app.home');
            } else {
                console.log("Login failed")
                $scope.authError = true;
                $rootScope.authenticated = false;
                authService.setAuthenticated(false);
            }
        })
    };
  }])
;