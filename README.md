### With TempVoicer, creating and managing temporary VoiceChannels has never been easier!
TempVoicer will wait for you to join the designated Lobby channel and will then create a new voice channel in either a designated category or just after the last channel.

## Invite Link
[Click here](https://discordapp.com/oauth2/authorize/?permissions=17918032&scope=bot&client_id=581758105000607767) to invite TempVoicer to your discord guild

## Setting up TempVoicer
|     Step     | Description                                                                                                                                                  | Example Command                                       |
|:------------:|--------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
|       1      | Invite the Bot to the server and assign it all permissions that it requests with the invite                                                                  |                                                       |
|       2      | Define a lobby channel. This channel must already exist. You define it by issuing `voice!property voicer.lobby.id <ID>`                                      | `voice!property voice.lobby.id 488124623234531358`    |
| 3 (Optional) | Define a category in which the channels should be created. This category must already exist. You define it by using `voice!property voicer.category.id <ID>` | `voice!property voice.category.id 479438201887522836` |

Note: To be able to use `voice!property`, you must be able to effectively use the `Manage Server` permission.

After you have set up the lobby channel, when joining the lobby, TempVoicer will create a new VoiceChannel and move you into it.
From there, you can change all parameters of the VoiceChannel as listed below using the `VoiceChannel commands`.
When all users leave the channel, TempVoicer will delete the channel.

## All TempVoicer commands
### VoiceChannel commands
| Command                 | Description                                                            | Notes                                                     |
|-------------------------|------------------------------------------------------------------------|-----------------------------------------------------------|
| `voicer!lock`           | Locks your voice channel for all other users                           | Users that are already in the channel stay in the channel |
| `voicer!unlock`         | Unlocks your voice channel for all other users                         |                                                           |
| `voicer!name <Name>`    | Changes the name of your voice channel                                 |                                                           |
| `voicer!limit <Number>` | Changes the user limit of your voice channel to the given number       | Fails if the parameter is not an Integer                  |
| `voicer!permit <User>`  | Locks your voice channel for all other users except the mentioned user | BETA: Might not work properly                             |
| `voicer!reject <User>`  | Disallows the mentioned user from joining your voice channel           | BETA: Might not work properly                             |
### Other commands
| Command                                         | Description                                                           | Notes                                             |
|-------------------------------------------------|-----------------------------------------------------------------------|---------------------------------------------------|
| `voicer!help [Command]`                         | Shows all available commands, or information about a specific command |                                                   |
| `voicer!property [<Property Name> [New Value]]` | Setup command                                                         | Requires the `Manage Server` permission for usage |
| `voicer!invite`                                 | Sends you an Invite-link for the bot via DM                           |                                                   |
