//定义服务
app.service('brandService', function ($http) {

    this.search = function (page, rows, searchEntity) {
        return $http.post('../brand/search.do?page=' + page + "&rows=" + rows, searchEntity);
    }

    this.save = function (method, entity) {
        return $http.post('../brand/' + method + '.do', entity)
    }

    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    }

    this.delete = function (selectIds) {
        return $http.get('../brand/delete.do?ids=' + selectIds)
    }

    //下拉框数据
    this.selectOptionList = function () {
        return $http.get('../brand/selectOptionList.do');
    }

});