# Contribution Guidelines

Thanks for your interest in contributing this project. All issues and pull requests are highly appreciated!

## Pull Requests

Before sending a pull request to this project, please read and follow guidelines:

1. Branch: We only accept pull requests on the `master` branch
2. Coding style: Follow the coding style used in Turbo
3. Commit message: Use English and check your spelling
4. Test: Make sure to test your code

Add device mode, API version, related log, screenshots and other related information in your pull request if possible.

NOTE: We assume all contributions can be licensed under the [Apache License 2.0](LICENSE).

## Issues

We love clearly described issues. :)

The following information can help us resolve the issue faster:

* Device mode and hardware information
* API version
* Logs
* Screenshots
* Steps to reproduce the issue

---

# 如何参与Turbo社区贡献

## 如何贡献？

在贡献代码之前，请您稍微花一些时间了解为 Turbo 贡献代码的流程。熟悉和参与 Turbo 社区维护，最直接、快速的方式就是参与 issue 列表讨论，issue访问地址：https://github.com/didi/turbo/issues

## 贡献什么？

无论是简单的错别字修正还是BUG 修复、代码优化或者是新功能的添加，我们都随时欢迎您的贡献，请踊跃提出问题或发起 PR。除此之外，我们同样很重视文档以及与其它开源项目的整合，欢迎在这方面做出贡献。

如果是一个比较复杂的修改，建议先在 Issue 中添加一个 Feature 标识，并简单描述一下设计和修改点。

## 从哪里入手？

如果您是初次贡献，可以先从 [good first issue](https://github.com/didi/turbo/labels/good%20first%20issue) 和 [help wanted](https://github.com/didi/turbo/labels/help%20wanted) 中认领一个比较简单的任务。

## Fork 仓库，并将其 Clone 到本地

- 点击 [本项目](https://github.com/didi/turbo) 右上角的 `Fork` 图标 将 didi/turbo  fork 到自己的空间。
- 将自己账号下的 turbo 仓库 clone 到本地，例如我的账号的 `lch`，那就是执行 `git clone https://github.com/lch/turbo.git` 进行 clone 操作。

## 配置 Github 信息

- 在自己的机器执行 `git config  --list` ，查看 git 的全局用户名和邮箱。
- 检查显示的 user.name 和 user.email 是不是与自己 github 的用户名和邮箱相匹配。
- 如果公司内部有自己的 gitlab 或者使用了其他商业化的 gitlab，则可能会出现不匹配的情况。这时候，你需要为 turbo 项目单独设置用户名和邮箱。
- 设置用户名和邮箱的方式请参考 github 官方文档，[设置用户名](https://help.github.com/articles/setting-your-username-in-git/#setting-your-git-username-for-a-single-repository) ，[设置邮箱](https://help.github.com/articles/setting-your-commit-email-address-in-git) 。

## merge 最新代码

fork 出来的代码后，原仓库 Master 分支可能出现了新的提交，这时候为了避免提交的 PR 和 Master 中的提交出现冲突，需要及时 merge master 分支。

- 在你本机的 turbo 目录下，执行 `git remote add upstream https://github.com/didi/turbo` 将原始仓库地址添加到 remote stream 中。
- 在你本机的 turbo 目录下，执行 `git fetch upstream` 将 remote stream fetch 到本地。
- 在你本机的 turbo 目录下，执行 `git checkout master` 切换到 master 分支。
- 在你本机的 turbo 目录下，执行 `git rebase upstream/master` rebase 最新代码。

## 配置 Turbo 标准的代码格式

Turbo项目有自己独有的代码格式规范，提交代码前需要先配置好代码格式规范。

[turbo-code-style.xml](turbo-code-style.xml)

## 开发、提交、Push

开发自己的功能，所有代码改动必须提供单测，开发完毕后建议使用 `mvn clean package` 命令确保能修改后的代码能在本地编译通过。

## merge 最新代码

- 同样，提交 PR 前，需要 rebase master 分支的代码，具体操作步骤请参考之前的章节。
- 如果出现冲突，需要先解决冲突。

```
git checkout master  
git pull origin master 
git checkout [自己的分支名] # 切换到自己的分支
git rebase master 变基， 合并后自己的分支为最新的代码
git push # 提送自己本地分支到自己的远程分支
```

## 提交PR

提交 PR，根据 `Pull request template` 写明修改点和实现的功能，等待 code review 和 合并，成为 Turbo contributor，为更好用的 Turbo 做出贡献。

### Pull Request约定

> Pull reuqest命名格式： [ISSUE #{issue number}] body
> 
> 需要注意ISSUE和其数字编号之间需要有一个空格，方括号与body之间也需要有空格，body的首字母大写。
> 
> Pull request描述格式：
> 
> What is the purpose of the change
> 
> XXXXXX
> 
> Brief changelog
> 
> XXXXXX
> 
> Verifying this change
> 
> XXXXXX

### Commit Messages 格式约定

> Commit Message包含对提交记录的简洁描述，包括类型、可选范围（模块）和简单描述。
> 
> Commit Message格式：`<type>(<scope>): <body>`
> 
> `<type>: ` 描述commit更改属于什么类型，可选的类型包括：
> 
> feat： 新增 feature
> 
> fix: 修复 bug
> 
> docs: 仅仅修改了文档，比如 README, CHANGELOG, CONTRIBUTE等等
> 
> style: 仅仅修改了空格、格式缩进、逗号等等，不改变代码逻辑
> 
> refactor: 代码重构，没有加新功能或者修复 bug
> 
> perf: 优化相关，比如提升性能、体验
> 
> test: 测试用例，包括单元测试、集成测试等
> 
> chore: 改变构建流程、或者增加依赖库、工具等
> 
> revert: 回滚到上一个版本
> 
> `<body>: `作用域，表示commit更改所属的模块。例如log, remoting, rpc, client, console, plugin, storage等，如果没有更合适的范围，你可以用 *。
> 
> 对Commit更改简短的描述，需要注意以下几点：
> 使用动宾结构，注意使用现在时，比如使用change 而非 changed 或 changes
> 
> 首字母不要大写
> 
> 语句最后不需要 ‘.’ (句号) 结尾
> 
> 举例：docs(guide): update the developer guide