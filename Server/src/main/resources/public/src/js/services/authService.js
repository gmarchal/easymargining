'use strict';

angular.module('authService', [])
    .constant('AUTH_EVENTS', {
        loginSuccess: 'auth-login-success',
        loginFailure: 'auth-login-failed',
        logoutSuccess: 'auth-logout-success',
        sessionTimeout: 'auth-session-timeout',
        notAuthenticated: 'auth-not-authenticated',
        notAuthorized: 'auth-not-authorized' })
    .constant('USER_ROLES', {
        all: '*',
        admin: 'admin',
        editor: 'editor',
        guest: 'guest'})
    .service('Session', function() {
            this.create = function (userId, userFirstName, userLastName, userEmail, userRole) {
                console.log("Create Session with : " + userId + ", " + userFirstName +
                            ", " + userLastName + ", " + userEmail + ", " + userRole);
                this.userId = userId;
                this.userFirstName = userFirstName;
                this.userLastName = userLastName;
                this.userEmail = userEmail;
                this.userRole = userRole;
            }

            this.destroy = function() {
                this.userId = null;
                this.userFirstName = null;
                this.userLastName = null;
                this.userEmail = null;
                this.userRole = null;
            }
    })
	.factory('authService', ['$http', '$window', 'Session', function ($http, $window, Session) {

        var authService = {};

        authService.authenticate = function(credentials, callback) {

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
                    Session.create(data.principal.id,
                                    data.principal.firstName,
                                    data.principal.lastName,
                                    data.principal.email,
                                    data.principal.authorities);
                    $window.sessionStorage.currentUser = data.principal;
                } else {
                    console.log(data);
                }
                callback && callback(data);
            }).error(function() {
                // Erase the currentUser if the user fails to log in
                delete $window.sessionStorage.currentUser;

                callback && callback(false);
            });

        }

        authService.isAuthenticated = function() {
            return !!Session.username;
        }

        authService.isAuthorized = function(authorizedRoles) {
            if (!angular.isArray(authorizedRoles)) {
                authorizedRoles = [authorizedRoles];
            }
            return (authService.isAuthenticated() &&
                    authorizedRoles.indexOf(Session.userRole) !== -1);
        };

        return authService;
    }]);


