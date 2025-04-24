# Server++

> A Minecraft Neoforge mod about server technology options control

## Main Features

### View Distance

> Modify the server view distance

* usage: `/rg viewDistance <view distance>`

### Simulation Distance

> Modify the server simulation distance

* usage: `/rg simulationDistance <simulation distance>`

### `/here` Command

> Quickly send position

* enable: `/rg commandHere true`
* usage:
    * `/here`

### `/whereis` Command

> Quickly locate player

* enable: `/rg commandWhereis true`
* usage:
    * `/whereis <player>`
    * `/vris <player>`

### `/todo` Command

> A ToDo Management Menu

* enable: `/rg commandTodo true`
* usage:
    * `/todo` or `/todo list` to display the todo list
    * `/todo add <content>` to add a todo
    * `/todo remove <index>` to remove a todo
    * `/todo success <index>` to mark a todo as done
    * `/todo success <index> false` to unmark a todo as done

### `/loc` Command

> A Loc Management Menu

* enable: `/rg commandLoc true`
* usage:
    * `/loc` or `/loc list` to display the loc list
    * `/loc add <name>` to add a loc as player's position
    * `/loc remove <index>` to remove a loc
    * `/loc info <index>` to display a loc info

### `/wlist` Command

> Whitelist Management

* enable: `/rg commandWlist true`
* usage:
    * `/wlist` to display the whitelist list
    * `/wlist add <player>` to add a player to the whitelist
    * `/wlist remove <player>` to remove a player from the whitelist
    * `/wlist permission add <player>` to make a player can use the whitelist command
    * `/wlist permission remove <player>` to make a player can't use the whitelist command

### `/blist` Command

> Banned List Management

* enable: `/rg commandBlist true`
* usage:
    * `/blist` to display the banned list
    * `/blist add <player>` to add a player to the banned list
    * `/blist remove <player>` to remove a player from the banned list
    * `/blist permission add <player>` to make a player can use the banned list command
    * `/blist permission remove <player>` to make a player can't use the banned list command

### `/sop` Command

> Simple op get

* enable: `/rg commandSop true`
* usage:
    * `/sop` to get op permission

### `/transfer` Command

> Allows players to transfer themselves using the `/transfer` command

* enable: `/rg commandTransfer true`

### Fast Ping Friend

> Fast ping your friend

* enable: `/rg fastPingFriend true`
* usage:
    * `@ <player>` Alert your friends in the chat and play the sound of picking up experience orbs
    * `@@ <player>` Show the title to tip friends and play the sound of the bell

### Welcome Player

> Send welcome message when player login to server

* enable: `/rg welcomePlayer true`
