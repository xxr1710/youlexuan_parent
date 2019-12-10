app.controller('searchController', function ($scope,$location, searchService) {


    //加载查询字符串
    $scope.loadKeywords = function(){
        //获取9103 传递过来的keywords
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }


    //判断关键字是不是品牌
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) > 0) {
                //如果包含
                return true;
            }
        }
        return false;
    }


    //搜索条件封装对象
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 20,
        'sort': '',
        'sortField': ''
    };

    //构建分页标签（totalPages 总页数）
    buildPageLabel = function () {
        //新增分页栏属性
        $scope.pageLabel = [];
        //得到最后的页码
        var maxPageNo = $scope.resultMap.totalPages;
        //开始页码
        var firstPage = 1;
        //截止页码
        var lastPage = maxPageNo;

        //前面有点
        $scope.firstDot = true;
        //后面有点
        $scope.lastDot = true;

        //如果总页数大于5,显示部分页码
        if ($scope.resultMap.totalPages > 5) {
            //如果当前页小于3
            if ($scope.searchMap.pageNo <= 3) {
                //前5页
                lastPage = 5;

                //前面没点
                $scope.firstDot = false;

            } else if ($scope.searchMap.pageNo >= lastPage - 2) {//如果当前页大于等于最大页码 -2
                firstPage = maxPageNo - 4;  //后5页

                //后面没点
                $scope.lastDot = false;
            } else {
                //显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            //前面无点
            $scope.firstDot = false;
            //后面无点
            $scope.lastDot = false;
        }
        //循环显示页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.searchMap.pageNo = parseInt(pageNo);
        $scope.search();
    }

    //设置页码不可用样式
    //判断当前页为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }
    //判断当前页为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }

    //设置排序规则
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    }

    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        if ('category' == key || 'brand' == key || 'price' == key) {//如果是点击的分类或者是品牌
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //执行搜索
        $scope.search();
    };
    //移除复合搜索条件
    $scope.removeSearchItem = function (key) {
        if ('category' == key || 'brand' == key || 'price' == key) {//如果是分类或者品牌
            $scope.searchMap[key] = "";
        } else {//规格
            //移除
            delete $scope.searchMap.spec[key];
        }
        //执行搜索
        $scope.search();
    }

    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                $scope.list = response.rows;

                //调用分页方法
                buildPageLabel();
            }
        );
    }


});