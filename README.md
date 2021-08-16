General idea: write WS client that communicates via Binance API (https://binance-docs.github.io/apidocs/spot/en/).

What you need to do:
1. Create a console app that will be able to subscribe to common channels (that not require auth) like ticker and trades (you are free to add other channels too).
Command might look like this:
- "ACTION;CHANNEL;SYMBOL" 
where action = subscribe/unsubscribe; channel is binance channel (see API to find channels); SYMBOL - for example BTCUSDT
It could be done with any WS client jetty, java 11 or whatever you prefer, even spring.

2. All messages that you receive via WS you need to write to file.
3. After that we can upgrade our app and write data to file, but firstly encrypt and compress it.
4. Create a separate java class inside project that will be able to read data. Here we just need to reverse the process (decompress and decrypt the feed).
5. Let's complicate the task, we need to add our custom header to file(magic number, like in java CAFEBABE, but our own). Update your reader class also.
Let's assume that we can use one of two different headers, just to practice how to rewind stream while reading it. 

Good code organization and using OOP principles are welcome! Good Luck!
