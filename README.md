# Plotsblitz by KarlTheBee
A plugin for automatically creating and managing plots

## Functions
- incredibly fast through mysql database access
- allows multiple worlds with different plot sizes and costs per world
- through vault compatible with almost every currency plugin
- player can add friend to build together
- player can use worldedit in their plots - and only in their plots
- system plots allows using plots for public places


## Installation
- a mysql database is required
- obviously an up-to-date spigot server is needed
- vault is required
- an economy plugin compatible with vault is required

Place the plugin into the plugin folder and start your server.
If an error is thrown, please enter the right database credentials

## Usage

### Generate an world
You can use the world


### Create an world
You can create a world with /plot-world-create <world> <size> <cost> <crossing>
[world] is the world name you want to plot to be,
[size] is the plot size in chunks (!), which means "2" generates a (16*2=) 32 block plot in length and width, must be >0
[cost] is the cost of the plot. Can be a floating point number ("3.1415"), must be >= 0
[crossing] if you want crossings, set this value to be not 0. 1 means that after every plot is a crossing, 2 that after 2 plots and so on
### Buy an plot
You can buy the plot you're standing on with /plot-buy
You need to have enough money and the plot must be free
 
### Admin Mode
If you want to edit other plots as an admin (or maybe delete them?) you first have to go into admin mode.
Why? Because otherwise you would be an 24/7 bomb who can currupt every plots by mistake.

## License
This application is licensed unter Creative Commons CC-BY-NC