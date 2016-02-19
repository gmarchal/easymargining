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
    .service('Session', [ '$window', function($window) {

            var currentUser = {};
            restoreSession();

            this.create = function (userId, userFirstName, userLastName, userEmail, userRole) {
                console.log("Create Session with : " + userId + ", " + userFirstName +
                            ", " + userLastName + ", " + userEmail + ", " + userRole);

                currentUser = {
                    userId: userId,
                    userFirstName: userFirstName,
                    userLastName: userLastName,
                    userEmail: userEmail,
                    userRole: userRole
                }

                persistSession();
            }

            this.destroy = function() {
                // Erase the currentUser if the user fails to log in
                this.currentUser = null;
                delete $window.sessionStorage.currentUser;
            }

            this.getCurrentUser = function() {
                return currentUser;
            }

            //Persist to session storage
            function persistSession() {
                $window.sessionStorage.setItem('currentUser', JSON.stringify(currentUser));
            }

            //Restore to session storage
            function restoreSession() {
                currentUser = JSON.parse($window.sessionStorage.getItem('currentUser'));
                if (currentUser == null) {
                    //Initialize to empty
                    currentUser = {
                        userId: "",
                        userFirstName: "",
                        userLastName: "",
                        userEmail: "",
                        userRole: []
                    }
                }
            }
    }])
	.factory('authService', ['$http', 'Session', function ($http, Session) {

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
                } else {
                    console.log(data);
                }
                callback && callback(data);
            }).error(function() {
                Session.destroy();
                callback && callback(false);
            });

        }

        authService.isAuthenticated = function() {
            return !!Session.currentUser;
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


