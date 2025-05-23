# 提交信息规范

## 提交信息格式

每个提交信息包含一个**头部**、一个**正文**和一个**页脚**，头部有一个特定的格式，包括**类型**、**作用域**和**主题**：

```
<类型>(<作用域>): <主题>
<空行>
<正文>
<空行>
<页脚>
```

其中，**头部**是必需的，**作用域**是可选的。

提交信息中的任何一行都不能超过100个字符，这样可以使信息在GitHub和各种Git工具中更容易阅读。

### 类型

必须是以下类型之一：

* **feat**: 新功能
* **fix**: 修复Bug
* **docs**: 仅修改文档
* **style**: 不影响代码含义的更改（空格、格式化、缺少分号等）
* **refactor**: 代码重构，既不修复错误也不添加功能
* **perf**: 提高性能的代码更改
* **test**: 添加缺失或修正现有测试
* **chore**: 对构建过程或辅助工具和库的更改（如文档生成）

### 作用域

作用域可以是任何指定提交更改位置的东西，例如：

* **main**: 主进程相关
* **renderer**: 渲染进程相关
* **ui**: UI组件相关
* **api**: API相关
* **config**: 配置文件相关
* **deps**: 依赖相关

### 主题

主题包含对变更的简洁描述：

* 使用祈使句，现在时："change"而不是"changed"或"changes"
* 不要大写第一个字母
* 不要在末尾添加句号

### 正文

正文应该包括改变的原因，并将其与以前的行为进行对比。

### 页脚

页脚应该包含任何关于**Breaking Changes**（破坏性变更）的信息，也是引用GitHub问题的地方。

Breaking Changes 应该以 `BREAKING CHANGE:` 开头，后跟一个空格或两个换行符，其余的信息就是对变更的描述、理由和迁移说明。

### 示例

```
feat(profile): 添加用户资料编辑功能

增加了用户编辑个人资料的功能，包括修改昵称、头像和个人简介。

Closes #123
```

```
fix(auth): 修复登录页面在移动设备上的显示问题

修复了登录页面在小屏幕设备上按钮错位的问题。

修复 #456
```

```
chore(deps): 更新依赖版本

将 electron 更新到 v25.6.0
将 vue 更新到 v3.3.4

BREAKING CHANGE: 新版本的Electron需要Node.js 16+
```

## 工具推荐

为了帮助团队成员编写符合规范的提交信息，建议使用以下工具：

- [commitizen](https://github.com/commitizen/cz-cli): 交互式提交信息生成工具
- [commitlint](https://github.com/conventional-changelog/commitlint): 提交信息校验工具

## 安装与配置

可以通过以下命令安装commitizen：

```bash
npm install -g commitizen
npm install --save-dev cz-conventional-changelog
```

然后在package.json中添加配置：

```json
{
  "scripts": {
    "commit": "git-cz"
  },
  "config": {
    "commitizen": {
      "path": "cz-conventional-changelog"
    }
  }
}
```

之后，可以使用`npm run commit`代替`git commit`来提交代码，会有交互式界面帮助编写规范的提交信息。 