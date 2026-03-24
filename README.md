# wipu

This is the Quarkus backend deployed at https://map.123k.org
It drives my private homepage https://123k.org

The name dates back to 2011 where I created a first `wipu` version in Ruby on Rails.
I completely forgot why I named it like this, but somehow I fear I just randomly pressed
 some keys. 

## API

There is actually no general purpose API available, but
most of the API can be used without any authentication.

* Emojis - as a service `xh -b https://map.123k.org/emoji?count=4` ( `count` is optional, default is 1)
* Geohash - XKCD Geohash locations for a date (only tested for central europe) `h -b https://map.123k.org/geohash/49/11/2026-02-28`
* Snaps - links to flickr images `xh -b https://map.123k.org/snaps`
* Tracks - My bike routes in an overview `xh -b https://map.123k.org/tracks`
* Track - One bike route with track-points `xh -b https://map.123k.org/tracks/6972926d65167b11904992f2`

There are a few more endpoints that need authentication. Those are infrastructure related.

## TODO

* Flickr Sync will fail with more than 500 photos on flickr (an generally this whole flickr stinks for the free-tier account)
* Prepare a proper track-creation client and commit it here

