// 定义模块:
var app = angular.module("youlexuan",[]);

app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);