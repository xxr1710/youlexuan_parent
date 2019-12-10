//商品详情页
app.controller("itemController", function ($scope, $http) {


    //数量操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    //记录用户选择的规格
    $scope.specificationItems = {};

    //用户选择规格
    $scope.selectSpecification = function (name, value) {
        $scope.specificationItems[name] = value;
        //读取SKU
        searchSku();
    }
    //网络：4G
    //判断某价格选项是否被用户选中
    $scope.isSelected = function (name, value) {
        if ($scope.specificationItems[name] == value) {
            return true;
        } else {
            return false;
        }
    }

    //加载默认SKU
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    };

    //匹配两个对象
    //{"网络":"移动3G","机身内存":"32G"} map1
    //{"网络":"移动4G","机身内存":"32G"} map2
    matchObject = function (map1, map2) {
        for (var key in  map1) {
            if (map1[key] != map2[key]) {
                return false;
            }
        }
        for (var key in  map2) {
            if (map2[key] != map1[key]) {
                return false;
            }
        }
        return true;
    }
    //查询SKU
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
    }
    //添加商品到购物车
    $scope.addToCart = function () {
        $http.get('http://localhost:9107/cart/addGoodsToCart.do?' +
            'itemId=' + $scope.sku.id + '&num=' + $scope.num,{'withCredentials':true}).success(
            function (response) {
                if (response.success){
                    //跳转到购物车页面
                    location.href='http://localhost:9107/cart.html';
                }else {
                    alert(response.message);
                }
            });
    }


});