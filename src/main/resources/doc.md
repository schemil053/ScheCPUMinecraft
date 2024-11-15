| IO Port | Description                                                                          |
|---------|--------------------------------------------------------------------------------------|
| 128     | 0 = Disable 129 Multi-Execution, 1 = enable                                          |
| 129     | if Multi-Execution is enabled, it defines the count of cycles per tick (can be 0!!!) |
| 130     | Broadcast (Buffer). Resets on \n. Will only be sent when set to \n                   |
| 131     | Broadcast reset (0 = reset)                                                          |
| 140     | Redstone wire down mode (below) 0=read, 1=write, 2=readAnalog, 3=writeAnalog         |
| 141     | Redstone wire down state (1-15) if Analog, 0 or 1 if digital                         |