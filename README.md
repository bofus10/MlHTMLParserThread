# MlHTMLParserThread

_ML Arg web parser: Will scrap all the vendors listed on your vendors table on your db_

## Pre-requisits 📋

* _You will need to compile the code and generate a jar file for execution_
* _Setup a DB_
* _Add sellers_

## Setup 🔧

_For this to work you will need to install and setup a DB, in my case I use mariaDB and then load the sellers you want to scrap from_

_DB Structure can be found under samples directory_

_Once DB is up and running, fill the sellers and you can launch your program_

```
java -jar compiled_jar
```

### Adding vendors 🛠️

_You need to fill the vendors table first. This table has 3 columns ID,name,display_

_ID: Any ID You want to give to the seller for your internal usage, ID is not an autoincrement this is due to the program_
_not being able to scrap all of seller listing at once due to ML AuthToken based authorizartion, to sort this we filter_
_within the seller's list by price and work in up to 1000 products batches_

_name: Seller_ID, this can be gather from any of the products page of the seller, inspecting it and looking for seller_id_

_display: This is the Display Name you want the app to show to you, if seller_id = 12345678, display=PCWorld_

__example__
```
seller_id = 12345678

If seller has more than 1000 products we can: 

12345678&price=*-5000 #from 0 to 5000$

12345678&price=5000-* #from 5000 to MAX

Both records need to have the SAME ID.
```

### Setup Telegram Bot ⚙️

_You will need to setup a Telegram bot for it to send you updates and mesages_
_You can follow https://core.telegram.org/bots_

_Once you got your bot setup, you need to save your API_KEY_
_You will need to get your chat_id to set to where you want to send messages to_
_To do this, best option is to open a chat with your new bot and send him a nice message_
_after that you can check whats your chat_id by checking what the bot recived on:_

```
https://api.telegram.org/botBOT_API_KEY/getUpdates

That will return a json file with all the info needed to process, like the chat_id
```

_Once you got you BOT_TOKEN and CHAT_ID you need to fill those on config/config.properties_
_This file will require 3 Bots and 2 Chats:_

* One Bot and Chat for regular message
* One Bot and Chat for special price message
* One Bot for custom searchs

### Content config.properties ⚙️
* sql_queue_size = SQL Queue Size
* thread_num = Number of threads to launch -> More threads = More data flow, make your your sql_queue_size is properly sized and your DB can handle the flow.

* perc_regular = Base Percentaje to trigger a regular msg alert
* perc_special = Base Percentaje to trigger an special msg alert


## Usage 🚀

_The usage is mainly via Telegram Bots, all message will get to you automatically_

_Search Bot is for you to search products within your database, return result size can be modified on config.properties_
_Open a chat with your search bot and type whatever you want to find sorted by lower price_

## Logging 🔩

_You can check logs folder to check if data is being inserted on the DB_

## License 📄

Project License type:  GPL-3.0 License - Check [LICENSE](LICENSE) file for more details.
