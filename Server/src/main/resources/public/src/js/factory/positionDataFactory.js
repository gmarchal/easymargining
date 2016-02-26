angular.module('app')
  .factory('PositionDataFactory', ['$http', 'Session', function ($http, Session) {

    var positions = [];
    var portfolioId = null;

    var productIds = [
    			{ eurexCode: 'ORDX' },
    			{ eurexCode: 'OGB1' },
    			{ eurexCode: 'VVU' },
    		];
    /*
    var productIds = [];
    $http.get("/api/product/alldef")
        .success(function(data) {
            console.log(data);
            productIds=data;
        })
        .error(function(data, status) {
            console.error('Error', status, data);
        });*/

    var optionTypes = [
    			{ id: 'C', name: 'Call'},
    			{ id: 'P', name: 'Put'},
    		];

    var instrumentTypes = [
              			{ id: 'Future' , name: 'Future'},
              			{ id: 'Option' , name: 'Option'},
              			{ id: 'Flex Option' , name: 'Flex Option'}
              		];

     var settlementTypes = [
             			{ id: 'C' , name: 'Cash Settlement'},
             			{ id: 'E' , name: 'Physical Settlement'},
             			{ id: 'D' , name: 'Derivative'},
             			{ id: 'N' , name: 'Notional Settlement'},
             			{ id: 'P' , name: 'Payment-versus-payment'},
             			{ id: 'S' , name: 'Stock'},
             			{ id: 'T' , name: 'Cascade'},
             			{ id: 'T' , name: 'Alternate'},
             		];

     var exerciseStyleFlags = [
    				{ id: 'AMERICAN' , name: 'AMERICAN'},
    				{ id: 'EUROPEAN' , name: 'EUROPEAN'},
    			];

     return {
        positions: positions,
        portfolioId: portfolioId,
        productIds: productIds,
        optionTypes: optionTypes,
        instrumentTypes: instrumentTypes,
        settlementTypes: settlementTypes,
        exerciseStyleFlags: exerciseStyleFlags
     }
  }])
  .factory('ProductReferentialFactory', ['$http', 'Session', function ($http, Session) {
      var factory = {};
      factory.getProductIds = function(){
          return $http.get("/api/product/alldef");
      };
      factory.getMaturities = function(productId) {
          console.log(productId);
          return $http.get("/api/product/getMaturities",
                            { params: { productId: productId } } );
      };
      factory.getStrikes = function(productId, maturityYear, maturityMonth) {
          return $http.get("/api/product/getStrikes",
                            { params: { productId: productId, maturityYear: maturityYear, maturityMonth: maturityMonth } } );
      };
      return factory;
  }]);