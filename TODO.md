# TODO

Half-baked ideas and notes on where to proceed.

* Create a Logger abstraction that passed around or accessible from static method somewhere.
* Add generic settings that can be looked up in actions. settings may be
  - Is rbenv enabled? This will control whether `rbenvExec` method is called or skipped.
  - Is nodenv enabled?
  - Is Gemfile present? This will control whether `bundleExec` method is called or skipped.
