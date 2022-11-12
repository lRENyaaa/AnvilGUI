# AnvilGUI
此分支仅为验证在铁砧GUI中同时自定义3个格子内物品的可能性  
支持的版本: 1.11-1.19，需要ProtocolLib辅助
# 为何需要ProtocolLib
在PrepareAnvilEvent时就修改物品可以实现自定义格子内的物品  
但是只要你尝试使用上方输入框,就会使得输出栏的物品消失  
实际上物品并不是真的消失了,使用 updateInventory() 方法就会刷新并恢复  
于是使用ProtocolLib监听玩家使用输入框相关的数据包  
然后在玩家使用输入框时刷新背包即可解决