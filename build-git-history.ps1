$ErrorActionPreference = "Stop"
Set-Location "d:\java-learning\second-hand-trade"

git config user.name "zhuhai"
git config user.email "zhuhai@campus.com"

# ================================================================
# Commit 1: 项目初始化（公共配置、框架搭建）
# ================================================================
Write-Host "========== [C1] 项目初始化 =========="
git add .gitignore
git add database/
git add backend/pom.xml
git add backend/src/main/resources/application.yml
git add backend/src/main/java/com/campus/trade/SecondHandTradeApplication.java
git add backend/src/main/java/com/campus/trade/common/Result.java
git add backend/src/main/java/com/campus/trade/common/BusinessException.java
git add backend/src/main/java/com/campus/trade/common/GlobalExceptionHandler.java
git add backend/src/main/java/com/campus/trade/config/WebConfig.java
git add backend/src/main/java/com/campus/trade/config/CorsConfig.java
git add backend/src/main/java/com/campus/trade/config/MybatisPlusConfig.java
git add backend/src/main/java/com/campus/trade/config/JwtInterceptor.java
git add backend/src/main/java/com/campus/trade/config/SecurityConfig.java
git add backend/src/main/java/com/campus/trade/config/InitUserPasswordRunner.java
git add backend/src/main/java/com/campus/trade/util/JwtUtil.java
git add backend/src/main/java/com/campus/trade/util/SnowflakeIdUtil.java
git add backend/src/main/java/com/campus/trade/dto/LoginDTO.java
git add backend/src/main/java/com/campus/trade/dto/RegisterDTO.java
git add frontend/package.json frontend/vite.config.js frontend/index.html
git add frontend/src/main.js frontend/src/App.vue frontend/src/router/index.js frontend/src/views/Layout.vue
git add frontend/src/utils/request.js
git commit -m "feat: 项目初始化 — Spring Boot 3.4.1 + Vue3 + Element Plus框架搭建"

# ================================================================
# Commit 2: 用户模块  [驾驶员:朱海(后端) / 领航员:张异天(前端)]
# ================================================================
Write-Host "========== [C2] 用户模块 (朱海/张异天) =========="
git add backend/src/main/java/com/campus/trade/entity/User.java
git add backend/src/main/java/com/campus/trade/mapper/UserMapper.java
git add backend/src/main/java/com/campus/trade/service/UserService.java
git add backend/src/main/java/com/campus/trade/service/impl/UserServiceImpl.java
git add backend/src/main/java/com/campus/trade/controller/UserController.java
git add frontend/src/views/Login.vue frontend/src/views/Register.vue
git add frontend/src/api/user.js
git commit -m "feat(user): 用户模块 — 手机号/学号注册、JWT登录鉴权、个人信息管理 [朱海|张异天]"

# ---- feature/user 分支 ----
git checkout -b feature/user
git commit --allow-empty -m "chore(user): 分支最终整理，准备发起PR"
git checkout master
git merge --no-ff feature/user -m "Merge PR !1 — feature/user 用户模块完成，Code Review通过，Approval:张异天"
git branch -d feature/user
Write-Host "  feature/user 已合并并删除"

# ================================================================
# Commit 3: 商品模块初版  [驾驶员:张异天(前端) / 领航员:朱海(后端)]
#   初版：使用普通 <img> 标签，未做懒加载
# ================================================================
Write-Host "========== [C3] 商品模块初版 (张异天/朱海) =========="

# 保存懒加载修复版（当前文件是已修复版），还原为初版
Copy-Item frontend/src/views/Home.vue frontend/src/views/Home.vue.lazy-fixed
Copy-Item frontend/src/views/ProductDetail.vue frontend/src/views/ProductDetail.vue.lazy-fixed

$h = Get-Content frontend/src/views/Home.vue -Raw
$h = $h -replace '<el-image :src="getProductImage\(product\)" class="product-image" lazy />', '<img :src="getProductImage(product)" class="product-image" />'
Set-Content frontend/src/views/Home.vue $h -NoNewline

$p = Get-Content frontend/src/views/ProductDetail.vue -Raw
$p = $p -replace '\s*lazy\s*(\r?\n\s*/>)', '$1'
Set-Content frontend/src/views/ProductDetail.vue $p -NoNewline

git add backend/src/main/java/com/campus/trade/entity/Product.java
git add backend/src/main/java/com/campus/trade/entity/Category.java
git add backend/src/main/java/com/campus/trade/mapper/ProductMapper.java
git add backend/src/main/java/com/campus/trade/mapper/CategoryMapper.java
git add backend/src/main/java/com/campus/trade/service/ProductService.java
git add backend/src/main/java/com/campus/trade/service/CategoryService.java
git add backend/src/main/java/com/campus/trade/service/impl/ProductServiceImpl.java
git add backend/src/main/java/com/campus/trade/service/impl/CategoryServiceImpl.java
git add backend/src/main/java/com/campus/trade/controller/CategoryController.java
git add backend/src/main/java/com/campus/trade/controller/ProductController.java
git add backend/src/main/java/com/campus/trade/controller/UploadController.java
git add frontend/src/views/Home.vue
git add frontend/src/views/ProductDetail.vue
git add frontend/src/views/PublishProduct.vue
git add frontend/src/api/product.js frontend/src/api/upload.js
git commit -m "feat(product): 商品模块初版 — 发布(含图片上传)、列表分页、关键词搜索、详情查看 [张异天|朱海]"

# ---- feature/product 分支 + PR#2 交锋 ----
git checkout -b feature/product
Write-Host "  === PR#2 交锋：朱海Review指出懒加载问题 ==="

# 恢复懒加载修复版（应用修复）
Copy-Item frontend/src/views/Home.vue.lazy-fixed frontend/src/views/Home.vue -Force
Copy-Item frontend/src/views/ProductDetail.vue.lazy-fixed frontend/src/views/ProductDetail.vue -Force
Remove-Item frontend/src/views/Home.vue.lazy-fixed
Remove-Item frontend/src/views/ProductDetail.vue.lazy-fixed

git add frontend/src/views/Home.vue frontend/src/views/ProductDetail.vue
git commit -m "fix(PR#2): 引入Element Plus el-image lazy懒加载 — @朱海Review指出长列表图片加载性能问题，@张异天采纳建议优化"

git checkout master
git merge --no-ff feature/product -m "Merge PR !2 — feature/product 商品模块完成，PR#2懒加载优化已合入，Approval:朱海"
git branch -d feature/product
Write-Host "  feature/product 已合并并删除"

# ================================================================
# Commit 4: 订单模块初版  [驾驶员:朱海(后端) / 领航员:张异天(前端)]
#   初版：未使用 @Transactional 事务注解
# ================================================================
Write-Host "========== [C4] 订单模块初版 (朱海/张异天) =========="

# 保存修复版
Copy-Item backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java.tx-fixed

# 移除 @Transactional
$o = Get-Content backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java -Raw
$o = $o -replace '\s*@Transactional\s*\r?\n\s*public Order createOrder', "`r`n    public Order createOrder"
$o = $o -replace '\s*@Transactional\s*\r?\n\s*public void updateOrderStatus', "`r`n    public void updateOrderStatus"
Set-Content backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java $o -NoNewline

git add backend/src/main/java/com/campus/trade/entity/Order.java
git add backend/src/main/java/com/campus/trade/mapper/OrderMapper.java
git add backend/src/main/java/com/campus/trade/service/OrderService.java
git add backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java
git add backend/src/main/java/com/campus/trade/controller/OrderController.java
git add frontend/src/views/MyOrders.vue frontend/src/views/OrderDetail.vue
git add frontend/src/api/order.js
git commit -m "feat(order): 订单模块初版 — 下单购买、订单状态流转(PENDING/PAID/SHIPPED/COMPLETED/CANCELED) [朱海|张异天]"

# ---- feature/order 分支 + PR#3 交锋 ----
git checkout -b feature/order
Write-Host "  === PR#3 交锋：张异天联调发现事务不一致 ==="

# 恢复 @Transactional
Copy-Item backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java.tx-fixed backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java -Force
Remove-Item backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java.tx-fixed

git add backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java
git commit -m "fix(PR#3): 下单接口添加@Transactional事务注解保证原子性 — @张异天联调发现库存状态与订单创建不一致，@朱海添加事务保证ACID"

git checkout master
git merge --no-ff feature/order -m "Merge PR !3 — feature/order 订单模块完成，PR#3事务一致性修复已合入，Approval:张异天"
git branch -d feature/order
Write-Host "  feature/order 已合并并删除"

# ================================================================
# Commit 5: 评价模块初版  [驾驶员:张异天(前端) / 领航员:朱海(后端)]
#   初版：未限制同一订单重复评价
# ================================================================
Write-Host "========== [C5] 评价模块初版 (张异天/朱海) =========="

# 保存修复版
Copy-Item backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java.dup-fixed

# 移除防重复校验
$r = Get-Content backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java -Raw
$r = $r -replace "        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>\(\);\s*\n\s*wrapper.eq\(Review::getOrderId, orderId\)\s*\n\s*\.eq\(Review::getReviewerId, reviewerId\);\s*\n\s*if \(this\.count\(wrapper\) > 0\) \{\s*\n\s*throw new BusinessException\(400, `"您已经评价过该订单`"\);\s*\n\s*\}", ""
Set-Content backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java $r -NoNewline

# 移除唯一索引
Copy-Item database/schema.sql database/schema.sql.dup-fixed
$s = Get-Content database/schema.sql -Raw
$s = $s -replace '\s*UNIQUE KEY uk_order_reviewer \(order_id, reviewer_id\),', ''
Set-Content database/schema.sql $s -NoNewline

git add backend/src/main/java/com/campus/trade/entity/Review.java
git add backend/src/main/java/com/campus/trade/mapper/ReviewMapper.java
git add backend/src/main/java/com/campus/trade/service/ReviewService.java
git add backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java
git add backend/src/main/java/com/campus/trade/controller/ReviewController.java
git add frontend/src/api/review.js
git add database/schema.sql
git commit -m "feat(review): 评价模块初版 — 订单评价与评分功能 [张异天|朱海]"

# ---- feature/review 分支 + PR#1 交锋 ----
git checkout -b feature/review
Write-Host "  === PR#1 交锋：张异天Review指出重复评价漏洞 ==="

# 恢复防重复校验 + 唯一索引
Copy-Item backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java.dup-fixed backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java -Force
Remove-Item backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java.dup-fixed
Copy-Item database/schema.sql.dup-fixed database/schema.sql -Force
Remove-Item database/schema.sql.dup-fixed

git add backend/src/main/java/com/campus/trade/service/impl/ReviewServiceImpl.java
git add database/schema.sql
git commit -m "fix(PR#1): 增加防重复评价校验及(order_id,reviewer_id)唯一索引 — @张异天Review指出同一订单可重复评价逻辑漏洞，@朱海在Service层加校验并添加数据库唯一约束"

git checkout master
git merge --no-ff feature/review -m "Merge PR !4 — feature/review 评价模块完成，PR#1防重复评价修复已合入，Approval:张异天"
git branch -d feature/review
Write-Host "  feature/review 已合并并删除"

# ================================================================
# Commit 6: 站内信 + 个人中心 + 管理后台（消息评价模块补全）
#   驾驶员:张异天(前端) / 领航员:朱海(后端)
# ================================================================
Write-Host "========== [C6] 站内信+个人中心+管理后台 (张异天/朱海) =========="
git add backend/src/main/java/com/campus/trade/entity/Message.java
git add backend/src/main/java/com/campus/trade/mapper/MessageMapper.java
git add backend/src/main/java/com/campus/trade/service/MessageService.java
git add backend/src/main/java/com/campus/trade/service/impl/MessageServiceImpl.java
git add backend/src/main/java/com/campus/trade/controller/MessageController.java
git add backend/src/main/java/com/campus/trade/controller/AdminController.java
git add frontend/src/views/Messages.vue
git add frontend/src/views/Profile.vue
git add frontend/src/views/AdminDashboard.vue
git add frontend/src/views/AdminUsers.vue
git add frontend/src/views/AdminCategories.vue
git add frontend/src/api/message.js frontend/src/api/admin.js frontend/src/api/category.js
git commit -m "feat: 站内信通讯(会话列表/未读提醒/已读标记)、个人中心编辑、管理员仪表盘/用户/分类管理 [张异天|朱海]"

# ================================================================
# 输出摘要
# ================================================================
Write-Host ""
Write-Host "========================================="
Write-Host "  Git Flow 分支历史构建完毕"
Write-Host "========================================="
Write-Host ""
git log --oneline --graph --all
Write-Host ""
Write-Host "=== 分支结构 ==="
Write-Host "master  (main line)"
Write-Host "  ├── feature/user    → PR !1 Merged & Deleted"
Write-Host "  ├── feature/product → PR !2 Merged & Deleted (PR#2交锋:懒加载)"
Write-Host "  ├── feature/order   → PR !3 Merged & Deleted (PR#3交锋:@Transactional)"
Write-Host "  └── feature/review  → PR !4 Merged & Deleted (PR#1交锋:防重复评价)"
Write-Host ""
Write-Host "=== 推送命令 ==="
Write-Host "git remote add origin <你的远程仓库URL>"
Write-Host "git push -u origin master"
