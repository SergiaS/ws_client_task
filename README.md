# App Instruction

## Description
This app let you receiving the market data via websockets. It will be recording data into file, which will be encoded and compressed.

## Step-by-step
When you start main method, the app will get data access via WebSockets.

First, you need to subscribe for retrieving the data. 
The template is `ACTION;CHANNEL;SYMBOL`, for example type to the console `SUBSCRIBE;aggTrade;btcusdt`.

To stop receiving the data - type `stop` to the console.

In the final result you will get encoded and compressed websocket file data.
You can read this file - you need to use method `decompressAndDecrypt(filenameToRead)` in the `FileSecurityUtil`.

With class `StreamRewind` you can unzip file if you don't know which algorithm was use for compress.    
