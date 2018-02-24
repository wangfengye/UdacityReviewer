# Udacity Reviewes applys
* 第一步,不断请求api,发现可审阅项目,响铃提示;
* 发现可审阅项目户,提交申请,申请成功,toast 提示



## 
* ```QLiteDatabaseLockedException: database is locked```
    Cause:因为同时只能有一个SQLiteOpenHelper访问sqlite数据库,[参考](http://blog.csdn.net/u010002184/article/details/51508082)
    
* handler timer休眠状态下不执行

### 定时器 [ALarmManager](https://www.jianshu.com/p/d69a90bc44c0)


### BroadCast
 * 本地广播是无法通过静态注册
 
 ### 2018/2/24
 * 修复申请review请求;cause:请求体不同,需要将 form data 改为 request payload 
    > request payload 需要 改变请求头```Content-type```,以及自己组装请求体```requestBody```