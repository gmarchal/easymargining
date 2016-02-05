angular.module('app')
  .directive('uiPositionsTable', function($http, $filter) {
    return {
      restrict: 'EA',
	  scope: {
		  positions: '=',
		  portfolioId: '=',
		  valuationDate: '=',
	  },
	  replace: true,
	  templateUrl: 'tpl/table_positions.html',
      link: function(scope, elem, attrs) {
		  $http.get('data/positions.json').success(function(data) {
                scope.positions = data;
            });
      },
	  controller: function($scope, editableOptions, editableThemes) {
        editableThemes.bs3.inputClass = 'input-sm';
        editableThemes.bs3.buttonsClass = 'btn-sm';
        editableOptions.theme = 'bs3';

		$scope.productIds = [
			{ id: 'ORDX' },
			{ id: 'OGB1' },
			{ id: 'VVU' },
		];

		$scope.optionTypes = [
			{ id: 'C', name: 'Call'},
			{ id: 'P', name: 'Put'},
		];

		$scope.instrumentTypes = [
          			{ id: 'Future' , name: 'Future'},
          			{ id: 'Option' , name: 'Option'},
          			{ id: 'Flex Option' , name: 'Flex Option'}
          		];

        $scope.settlementTypes = [
         			{ id: 'C' , name: 'Cash Settlement'},
         			{ id: 'E' , name: 'Physical Settlement'},
         			{ id: 'D' , name: 'Derivative'},
         			{ id: 'N' , name: 'Notional Settlement'},
         			{ id: 'P' , name: 'Payment-versus-payment'},
         			{ id: 'S' , name: 'Stock'},
         			{ id: 'T' , name: 'Cascade'},
         			{ id: 'T' , name: 'Alternate'},
         		];

		$scope.exerciseStyleFlag = [
				{ id: 'AMERICAN' , name: 'AMERICAN'},
				{ id: 'EUROPEAN' , name: 'EUROPEAN'},
			];

		$scope.showProductIds = function(p) {
			var selected = [];
			if(p.productId) {
				selected = $filter('filter')($scope.productIds, {id: p.productId});
			}
			return selected.length ? selected[0].id : 'Not set';
		};

		$scope.showInstrumentTypes = function(p) {
				var selected = [];
				if(p.instrumentType) {
					selected = $filter('filter')($scope.instrumentTypes, {id: p.instrumentType});
				}
				return selected.length ? selected[0].name : 'Not set';
			};

		$scope.showOptionTypes = function(p) {
				var selected = [];
				if(p.productId) {
					selected = $filter('filter')($scope.optionTypes, {id: p.optionType});
				}
				return selected.length ? selected[0].name : 'Not set';
			};

		$scope.showSettlementTypes = function(p) {
                var selected = [];
                if(p.productId) {
                    selected = $filter('filter')($scope.settlementTypes, {id: p.productSettlementType});
                }
                return selected.length ? selected[0].name : 'Not set';
            };
			
		// Save Trade
		$scope.saveTrade = function(position, _id) {
			console.log(position);
			//$scope.position not updated yet
			angular.extend(position, {_id: _id});
			//Assign portfolio
			position.portfolioId = $scope.portfolioId;
			return $http({
						method: 'POST',
						url: '/position/save',
						data: position
					});
		};

		// remove trade
		$scope.removeTrade = function(index) {
			var positionsToRemove = $scope.positions[index];
			$scope.positions.splice(index, 1);
			return $http({
						method: 'POST',
						url: '/position/remove',
						data: positionsToRemove
					});
		};

		// add trade
		$scope.addTrade = function() {
			$scope.inserted = {
				portfolioId: $scope.portfolioId,
				productId: $scope.productIds.length ? $scope.productIds[0].id : 'Not set',      // First ProductId
				instrumentType: $scope.instrumentTypes[1].id,                                   // Option
				optionType: $scope.optionTypes.length ? $scope.optionTypes[0].id : 'Not set',   // Call
				expiryDate:  Date.now(),                                                        // ExpiryDate = Now + 3M
				productSettlementType: '',
				exercisePrice: 0,
				exerciseStyleFlag: '',
				quantity: 0,                                        							// 0
			};
			console.log($scope.inserted);
			$scope.positions.unshift($scope.inserted);
		};
	  }
    };
  });
