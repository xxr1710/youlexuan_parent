app.controller('brandController', function ($controller, $scope, $http, brandService) {

    //父控制器的继承
    $controller('baseController', {$scope: $scope})

    //添加数据
    $scope.save = function () {

        var id = $scope.entity.id;

        var methodName = "add";

        if (id != null) {
            methodName = "update";
        }

        brandService.save(methodName, $scope.entity).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }

            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };
    //创建要删除的id数组
    $scope.selectIds = [];  //选中的 id 集合
    $scope.updateSelection = function ($event, id) {

        if ($event.target.checked) { //如果是被选中，则添加到数组
            $scope.selectIds.push(id);
        } else {
            //如果取消选中，需要把它从数组里面删除
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);   //删除
        }
    };

    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(
            function (obj) {
                if (obj.success) {
                    $scope.reloadList();
                } else {
                    alert(obj.message);
                }
            }
        );
    }
    //模糊查询 + 分页
    $scope.searchEntity = {};   //定义搜索对象
    //条件查询
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;//总记录数
                $scope.list = response.rows;//给列表变量赋值
            }
        );
    }
})