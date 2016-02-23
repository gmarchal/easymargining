angular.module('app')
  .directive('uiPositionsTable', function($http, $filter) {
    return {
      restrict: 'EA',
	  scope: {
		  //positions: '=',
		  //portfolioId: '=',
		  //valuationDate: '=',
	  },
	  replace: true,
	  templateUrl: 'tpl/table_positions.html',
      link: function(scope, elem, attrs) {
		  //$http.get('data/positions.json').success(function(data) {
          //      scope.positions = data;
          //});
      },
	  controller: function($scope, editableOptions, editableThemes, PositionDataFactory, ProductReferentialFactory) {
        editableThemes.bs3.inputClass = 'input-sm';
        editableThemes.bs3.buttonsClass = 'btn-sm';
        editableOptions.theme = 'bs3';

        $scope.portfolioId = PositionDataFactory.portfolioId;
        $scope.positions = PositionDataFactory.positions;
		//$scope.productIds = PositionDataFactory.productIds;
	    ProductReferentialFactory.getProductIds()
	                             .success(function(data, status){
                                    $scope.productIds = data;
                                 });
		$scope.optionTypes = PositionDataFactory.optionTypes;
		$scope.instrumentTypes = PositionDataFactory.instrumentTypes;
        $scope.settlementTypes = PositionDataFactory.settlementTypes;
		$scope.exerciseStyleFlags = PositionDataFactory.exerciseStyleFlags;
		$scope.expiryDates = [];

		$scope.showProductIds = function(p) {
			var selected = [];
			if(p.productId) {
				selected = $filter('filter')($scope.productIds, {eurexCode: p.productId});
			}
			return selected.length ? selected[0].eurexCode : 'Not set';
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

		$scope.showExerciseStyle = function(p) {
            var selected = [];
            if(p.productId) {
                selected = $filter('filter')($scope.exerciseStyleFlags, {id: p.exerciseStyleFlag});
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

        $scope.showExpiryDates = function(p) {
            var selected = [];
            if(p.productId) {
                selected = $filter('filter')($scope.expiryDates, {id: p.expiryDate});
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
						url: '/api/positions/add',
						data: position
					});
		};

		// remove trade
		$scope.removeTrade = function(index) {
			var positionsToRemove = $scope.positions[index];
			$scope.positions.splice(index, 1);
			return $http({
						method: 'POST',
						url: '/api/positions/remove',
						data: positionsToRemove
					});
		};

		// add trade
		$scope.addTrade = function() {
			$scope.inserted = {
				portfolioId: $scope.portfolioId,
				productId: $scope.productIds.length ? $scope.productIds[0].eurexCode : 'Not set',      // First ProductId
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

		$scope.$watch(function() {
            return PositionDataFactory.portfolioId;
            }, function(value, last) {
                if (value) {
                    console.log(value)
                    $scope.portfolioId = PositionDataFactory.portfolioId
                } else {
                    $scope.portfolioId = null;
                }
            }
        );

        $scope.$watch(function() {
            return PositionDataFactory.positions;
            }, function(value, last) {
                if (value) {
                    console.log(value)
                    $scope.positions = PositionDataFactory.positions
                } else {
                    $scope.positions = [];
                }
            }
        );

        $scope.$watch(function() {
            return PositionDataFactory.productIds;
            }, function(value, last) {
                if (value) {
                    console.log(value)
                    $scope.productIds = PositionDataFactory.productIds
                } else {
                    console.log('Event on productIds.')
                    $scope.productIds = [];
                }
            }
        );

        $scope.refreshMaturities = function(productId) {
            console.log(productId);
            $scope.expiryDates = PositionDataFactory.getMaturities(productId);
            console.log($scope.expiryDates);
        };

        $scope.$watch(function() {
            return PositionDataFactory.productIds;
            }, function(value, last) {
                if (value) {
                    console.log(value)
                    $scope.productIds = PositionDataFactory.productIds
                } else {
                    console.log('Event on productIds.')
                    $scope.productIds = [];
                }
            }
        );
	  }
    };
  });
