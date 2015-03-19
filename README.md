# ThreeLicks
A crummy ticket server

This server is setup so that new keys are created on demand, and whenever a key is gotten, the next key 
(a long value) is returned. Somewhat based on memcached, though this is a quick trivial implementation.

The idea is just that "get key\r\n" will result in the next integer, with no chance of repeated values. No
method for reinitialization is available yet. Will be a read from files affair (likely a simple csv file).
