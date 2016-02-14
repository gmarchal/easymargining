'use strict';

angular.module('authService', [])
	.service('authService', ['$http', function ($http) {

    var principal = {};
    var authenticated = false;

    this.getPrincipal = function () {
        return principal;
    }

    this.setPrincipal = function (newPrincipal) {
        principal = newPrincipal;
    }

    this.isAuthenticated = function () {
            return authenticated;
    }

    this.setAuthenticated = function (newAuthenticated) {
        authenticated = newAuthenticated;
    }

}]);