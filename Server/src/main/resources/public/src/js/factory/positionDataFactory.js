angular.module('app')
  .factory('PositionDataFactory', ['$http', 'Session', function ($http, Session) {

    var positions = [];
    var portfolioId = null;

    var productIds = [
    			{ id: 'ORDX' },
    			{ id: 'OGB1' },
    			{ id: 'VVU' },
    		];

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
  }]);