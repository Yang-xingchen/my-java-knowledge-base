# 初始化
查看版本
```shell
git -v
```
配置用户名及邮箱
```shell
git config --global user.name "xxx"
git config --global user.email "xxx"
```
- 省略: 当前
- `--global`: 全局
- `--system`: 系统所有用户

# 初始化仓库
从本地本地目录
```shell
# 当前目录
git init
# 新建xxx目录
git init xxx
```
远程克隆
```shell
git clone xxx.git
```

# 工作区域
- 工作区(Working Directory)
- 暂存区(Staging Area/Index)
  - `git ls-files`: 查看暂存区内容
- 本地仓库(Local Repo)
- 远程仓库(Local Repo)

## 切换
工作区 => 暂存区
```shell
# .表示当前目录, 也可单独添加文件
git add . 
```
暂存区 => 本地仓库
```shell
git commit -m "xxx"
```
本地仓库 => 远程仓库
```shell
# 默认origin
git push
```
_暂存区 => 工作区_
```shell
git rm xxx
```
本地仓库 => 暂存区
```shell
git reset --soft
```
远程仓库 => 本地仓库
```shell
# 拉取并合并
git pull
# 拉取不合并
git fatch
```
本地仓库 => 工作区
```shell
git reset
```

## 查看状态
```shell
git status
```

## 比较内容
```shell
# 工作区 <==> 暂存区
git diff
# 工作区 <==> 本地仓库
git diff HEAD
# 暂存区 <==> 本地仓库
git diff --cache
# 不同版本
git diff commitId1 commitId2
# 不同分支
git diff branchName1 branchName2
```

# 提交
- `HEAD`最新版本
- `HEAD~`上一个版本
- `HEAD~N`上N个版本

```shell
git commit -m ""
```
- `-m` 提交内容
- `-a` 自动执行`git add .`命令

## 提交日志
```shell
git log
```

## 回退
### reset
分支回退，将分支归退到目标提交，该提交后的记录将会删除
```shell
git reset
```
| | 工作区 | 暂存区 | 说明 |
|---|---|---|---|
| --soft | 保留 | 保留 | 取消commit，将提交的变更回退到暂存区 |
| --hard | 丢弃 | 丢弃 | 完全回退到前一个commit状态，删除变更内容 |
| --mixed(默认参数) | 保留 | 丢弃 | 取消commit，将提交的变更回退到工作区 |

### revert
提交回退，将撤销目标分支的提交内容，添加新提交使用该修改
```shell
git revert
```

# .gitignore
在工作区根目录下添加该文件，可自动忽略文件内配置的文件内容
- 每行表示一类文件
- 文件可使用通配符`*`(任意个字符)`**`(中间目录)`?`(单个字符)`[]`(括号内字符，可使用`-`)`!`(取反)
- 文件夹使用`/`结尾
- `/`开头表示根目录
- 可用`#`开头表示注释
- **已跟踪的文件不会忽略, 如需忽略需`git rm --cache`**

# tag
添加{name}标记
```shell
git tag {name}
```

# 远程仓库
## 关联
```shell
# 添加
git remote add origin xxx.git
# 查询
git remote -v
```
- `origin`: 远程仓库名称
- `xxx.git`: 远程仓库地址

## 推送
```shell
git push {remote} {branchName}
```
- `{remote}`: 远程仓库名称, 默认`origin`
- `{branchName}`: 分支名称, 默认当前分支

# 分支
创建{name}分支
```shell
git branch {name}
```
删除{name}分支
```shell
# 删除已合并的分支
git branch -d {name}
# 强制删除分支
git branch -D {name}
```
切换{name}分支
```shell
# 可能有歧义
git chechout {name}
# 推荐
git switch {name}
```

## 合并
> 将另一条分支提交合并到当前分支，保留分支记录。
> 一般用于远程仓库与本地仓库。
```
*   [b1]git merge b2
|\
| * [b2]git commit
* | [b1]git commit
|/
*   [b1]git branch b2
```

合并{name}分支到当前分支
```shell
git merge {name}
```
中止合并
```shell
git merge --abort
```

## 变基
> 将当前分支分支点更改为目标当前位置。
> 一般用于本地仓库，不推荐用于远程仓库。
```
| * [b2]git commit
* | [b1]git commit
|/
*   [b1]git branch b2

=> [b2]git rebase b1

* [b2]git commit
| 
* [b1]git commit
|
* [b1]git branch b2
```