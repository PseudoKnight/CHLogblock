CHLogblock
==========

An extension for CommandHelper providing interactions with LogBlock

# Functions

## LBQueries
### lb_get_changes
**Returns**: array

**Args**: array

**Description**: Takes an associative array of parameters to query the database with
and returns an array of the matching changes. Block changes are in the form of
strings pre-formatted by LogBlock. The 'info' parameter allows you to specify columns"
to include data from. Columns available are CHESTACCESS, COORDS, COUNT,
DATA, DATE, ID, KILLER, MESSAGE, PLAYER, TYPE, VICTIM, and WEAPON.
The 'changetype' parameter can be ALL, BOTH, CHESTACCESS, CREATED,
DESTROYED, CHAT, KILLS, ENTITIES, ENTITIES_CREATED, or ENTITIES_KILLED.
The 'location' (with optional 'radius') can be included, or alternatively a 'sel' value
that can be either a pair of location corners or null to use the current WorldEdit selection.
The other available parameters are 'players', 'world', 'since', 'before', and 'limit'."

## LBLogging
### lb_log_break
**Returns**: void

**Args**: player, locationArray, [blockDataString]

**Description**: Manually logs a block being broken at a location.
You can choose to specify the BlockData of the block being broken.

### lb_log_place
**Returns**: void

**Args**: player, locationArray, [blockDataString]

**Description**: Manually logs a block being placed at a location.
You can choose to specify the BlockData of the block being placed.

### lb_log_break_sign
**Returns**: void

**Args**: playerName, locationArray, [frontLines], [backLines]

**Description**: Manually logs a sign being broken at a location.
The third and fourth params are arrays of lines for the front and back sides of the sign,
defaulting to current lines on the sign.
Note 1: An error will be thrown if the specified location is not a sign.
Note 2: Specifying lines will update the physical sign as well.

### lb_log_place_sign
**Returns**: void

**Args**: playerName, locationArray, [frontLines], [backLines]

**Description**: Manually logs a sign being placed at a location.
The third and fourth params are arrays of lines for the front and back sides of the sign,
defaulting to current lines on the sign.
Note 1: An error will be thrown if the specified location is not a sign.
Note 2: Specifying lines will update the physical sign as well.
