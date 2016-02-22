'use strict';

/**
 * Config for the router
 */
angular.module('app')
  .run(
    [ '$rootScope', '$state', '$stateParams', 'AUTH_EVENTS', 'authService',
      function ($rootScope,   $state,   $stateParams, AUTH_EVENTS, authService) {
          $rootScope.$state = $state;
          $rootScope.$stateParams = $stateParams;

          /*
          $rootScope.$on('$stateChangeStart', function (event, next) {
                var authorizedRoles = next.data.authorizedRoles;
                if (!authService.isAuthorized(authorizedRoles)) {
                      event.preventDefault();
                      if (authService.isAuthenticated()) {
                        // user is not allowed
                        $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
                      } else {
                        // user is not logged in
                        $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                      }
                }
          });
          */
      }
    ]
  )
  .config(
    [ '$httpProvider', '$stateProvider', '$urlRouterProvider', 'JQ_CONFIG', 'MODULE_CONFIG',
      function ($httpProvider, $stateProvider,   $urlRouterProvider, JQ_CONFIG, MODULE_CONFIG) {
          var layout = "tpl/app.html";

          if(window.location.href.indexOf("material") > 0){
            layout = "tpl/blocks/material.layout.html";
            $urlRouterProvider
              .otherwise('/app/dashboard-v3');
          }else{
            $urlRouterProvider
              .otherwise('/app/home');
          }
          
          $stateProvider
              .state('app', {
                  abstract: true,
                  url: '/app',
                  templateUrl: layout
              })
			  .state('app.home', {
                  url: '/home',
                  templateUrl: 'tpl/home.html',
                  resolve: load(['js/controllers/home.js'])
              })
              .state('app.admin', {
                    url: '/admin',
                    templateUrl: 'tpl/admin.html',
                    resolve: load(['js/controllers/admin.js'])
              })
			  .state('app.eurex-simulation', {
                  url: '/eurex-simulation',
                  templateUrl: 'tpl/app_eurex-simulation.html',
                  resolve: load(['ui.select', 'xeditable', 'js/controllers/eurex-simulation.js']) 
              })
			  .state('app.lch-simulation', {
                  url: '/lch-simulation',
                  templateUrl: 'tpl/under-construction.html',
              })
			  .state('app.reporting', {
                  url: '/lch-simulation',
                  templateUrl: 'tpl/under-construction.html',
              })
              // others
              .state('lockme', {
                  url: '/lockme',
                  templateUrl: 'tpl/page_lockme.html'
              })
              .state('access', {
                  url: '/access',
                  template: '<div ui-view class="fade-in-right-big smooth"></div>'
              })
              .state('access.signin', {
                  url: '/signin',
                  templateUrl: 'tpl/page_signin.html',
                  resolve: load( ['js/controllers/signin.js'] )
              })
              .state('access.signup', {
                  url: '/signup',
                  templateUrl: 'tpl/page_signup.html',
                  resolve: load( ['js/controllers/signup.js'] )
              })
              .state('access.forgotpwd', {
                  url: '/forgotpwd',
                  templateUrl: 'tpl/page_forgotpwd.html'
              })
              .state('access.404', {
                  url: '/404',
                  templateUrl: 'tpl/page_404.html'
              });

          $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

          function load(srcs, callback) {
            return {
                deps: ['$ocLazyLoad', '$q',
                  function( $ocLazyLoad, $q ){
                    var deferred = $q.defer();
                    var promise  = false;
                    srcs = angular.isArray(srcs) ? srcs : srcs.split(/\s+/);
                    if(!promise){
                      promise = deferred.promise;
                    }
                    angular.forEach(srcs, function(src) {
                      promise = promise.then( function(){
                        if(JQ_CONFIG[src]){
                          return $ocLazyLoad.load(JQ_CONFIG[src]);
                        }
                        angular.forEach(MODULE_CONFIG, function(module) {
                          if( module.name == src){
                            name = module.name;
                          }else{
                            name = src;
                          }
                        });
                        return $ocLazyLoad.load(name);
                      } );
                    });
                    deferred.resolve();
                    return callback ? promise.then(function(){ return callback(); }) : promise;
                }]
            }
          }


      }
    ]
  );
