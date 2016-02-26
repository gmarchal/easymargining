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
		$scope.exercisePrices = [];

        var monthNames = [
          "Jan", "Feb", "Mar",
          "Apr", "May", "Jun", "Jul",
          "Aug", "Sep", "Oct",
          "Nov", "Dec"
        ];
		$scope.formatExpiryDate = function (expiryDate) {
		    return expiryDate.contractYear + ' ' + monthNames[expiryDate.contractMonth-1]
		}

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
            if(p.optionType) {
                selected = $filter('filter')($scope.optionTypes, {id: p.optionType});
            }
            return selected.length ? selected[0].name : 'Not set';
        };

		$scope.showExerciseStyle = function(p) {
            var selected = [];
            if(p.exerciseStyleFlag) {
                selected = $filter('filter')($scope.exerciseStyleFlags, {id: p.exerciseStyleFlag});
            }
            return selected.length ? selected[0].name : 'Not set';
        };

        $scope.showSettlementTypes = function(p) {
            var selected = [];
            if(p.productSettlementType) {
                selected = $filter('filter')($scope.settlementTypes, {id: p.productSettlementType});
            }
            return selected.length ? selected[0].name : 'Not set';
        };

        $scope.showExpiryDates = function(trade) {
            //console.log('trade.expiryDates:', trade.expiryDates)
            var selected = [];
            if(trade.expiryDate) {
                selected = $filter('filter')(trade.expiryDates, {contractYear: trade.expiryDate.contractYear, contractMonth: trade.expiryDate.contractMonth});
            }
            //console.log('selected:', selected)
            if (!selected) {
                selected = [];
            }
            return selected.length ? $scope.formatExpiryDate(selected[0]) : 'Not set';
        };

        $scope.showExercisePrices = function(trade) {
            //console.log('trade.exercisePrices:', trade.exercisePrices)
            var selected = [];
            if(trade.exercisePrice) {
                selected = $filter('filter')(trade.exercisePrices, trade.exercisePrice);
            }
            //console.log('selected:', selected)
            if (!selected) {
                selected = [];
            }
            return selected.length ? selected[0] : 'Not set';
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
				instrumentType: $scope.instrumentTypes[1].id,                                          // Option
				optionType: $scope.optionTypes.length ? $scope.optionTypes[0].id : 'Not set',          // Call
				expiryDate:  Date.now(),                                                               // ExpiryDate = Now + 3M
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

        $scope.refreshMaturities = function(eurexCode) {
            console.log('product changed, new value of product.productId is: ', eurexCode);
        }

        $scope.loadMaturities = function(productId, trade) {
            trade.selectedProducId = productId || trade.productId;

            ProductReferentialFactory.getMaturities(trade.selectedProducId)
                                         .success(function(data, status){
                                            trade.expiryDates = angular.copy(data);
                                         });

            $scope.loadExercisePrices(trade.selectedProducId, trade.expiryDate, trade);
        };

        $scope.loadExercisePrices = function(productId, maturity, trade ) {
            trade.selectedProducId = productId || trade.selectedProducId || trade.productId;
            maturity = maturity || trade.expiryDate;

            console.log('----------------------------');
            console.log('>> new changes:', trade.selectedProducId, maturity);
            //console.log('>> new trade value:', trade);

            ProductReferentialFactory.getStrikes(trade.selectedProducId, maturity.contractYear, maturity.contractMonth)
                                         .success(function(data, status){
                                            trade.exercisePrices = angular.copy(data);
                                         });
        };

        // Temporaire
		//$scope.loadMaturities("ORDX");
		//$scope.loadExercisePrices("ORDX", {contractYear: 2022, contractMonth: 12})
	  }
    };
  });
