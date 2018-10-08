(ns nsorg.core-tests
  (:require [midje.sweet :refer :all]
            [nsorg.core :as nsorg]))

;; Default sorting rules

(fact
  "Sorts :refer-clojure :exclude option list"
  (nsorg/rewrite-ns-form "(ns sort-refer-clojure-exclude (:refer-clojure :exclude [map remove filter]))")
  => "(ns sort-refer-clojure-exclude (:refer-clojure :exclude [filter map remove]))")

(fact
  "Sorts :refer-clojure :only option list"
  (nsorg/rewrite-ns-form "(ns sort-refer-clojure-only (:refer-clojure :only [remove map filter]))")
  => "(ns sort-refer-clojure-only (:refer-clojure :only [filter map remove]))")

(fact
  "Sorts :refer-clojure :rename option map"
  (nsorg/rewrite-ns-form "(ns sort-refer-clojure-rename (:refer-clojure :rename {map m remove r filter f}))")
  => "(ns sort-refer-clojure-rename (:refer-clojure :rename {filter f map m remove r}))")

(fact
  "Sorts :require libspecs"
  (nsorg/rewrite-ns-form "(ns sort-require (:require [kissa.c :as c] kissa.b [kissa.a]))")
  => "(ns sort-require (:require [kissa.a] kissa.b [kissa.c :as c]))")

(fact
  "Sorts :require prefix libspecs"
  (nsorg/rewrite-ns-form "(ns sort-require-prefix (:require [kissa b [c :as c] a]))")
  => "(ns sort-require-prefix (:require [kissa a b [c :as c]]))")

(fact
  "Sorts :require libspec :refer option list"
  (nsorg/rewrite-ns-form "(ns sort-require-refer (:require [kissa.a :refer [b c a]]))")
  => "(ns sort-require-refer (:require [kissa.a :refer [a b c]]))")

(fact
  "Sorts :require prefix libspec :refer option list"
  (nsorg/rewrite-ns-form "(ns sort-require-prefix-refer (:require [kissa [a :refer [c a b]]]))")
  => "(ns sort-require-prefix-refer (:require [kissa [a :refer [a b c]]]))")

(fact
  "Sorts :use libspecs"
  (nsorg/rewrite-ns-form "(ns sort-use (:use kissa.b [kissa.c :exclude [x]] [kissa.a]))")
  => "(ns sort-use (:use [kissa.a] kissa.b [kissa.c :exclude [x]]))")

(fact
  "Sorts :use prefix libspecs"
  (nsorg/rewrite-ns-form "(ns sort-use-prefix (:use [kissa b [c :as c] a]))")
  => "(ns sort-use-prefix (:use [kissa a b [c :as c]]))")

(fact
  "Sorts :use libspec :exclude option list"
  (nsorg/rewrite-ns-form "(ns sort-use-exclude (:use [kissa.a :exclude [c b a]]))")
  => "(ns sort-use-exclude (:use [kissa.a :exclude [a b c]]))")

(fact
  "Sorts :use libspec :only option list"
  (nsorg/rewrite-ns-form "(ns sort-use-only (:use [kissa.a :only [c b a]]))")
  => "(ns sort-use-only (:use [kissa.a :only [a b c]]))")

(fact
  "Sorts :use libspec :rename option map"
  (nsorg/rewrite-ns-form "(ns sort-use-rename (:use [kissa.a :rename {b bar c baz a foo}]))")
  => "(ns sort-use-rename (:use [kissa.a :rename {a foo b bar c baz}]))")

(fact
  "Sorts :use prefix libspec :exclude option list"
  (nsorg/rewrite-ns-form "(ns sort-use-prefix-exclude (:use [kissa [a :exclude [c b a]]]))")
  => "(ns sort-use-prefix-exclude (:use [kissa [a :exclude [a b c]]]))")

(fact
  "Sorts :use prefix libspec :only option list"
  (nsorg/rewrite-ns-form "(ns sort-use-prefix-only (:use [kissa [a :only [c b a]]]))")
  => "(ns sort-use-prefix-only (:use [kissa [a :only [a b c]]]))")

(fact
  "Sorts :use prefix libspec :rename option map"
  (nsorg/rewrite-ns-form "(ns sort-use-prefix-rename (:use [kissa [a :rename {b bar c baz a foo}]]))")
  => "(ns sort-use-prefix-rename (:use [kissa [a :rename {a foo b bar c baz}]]))")

(fact
  "Sorts :import class names"
  (nsorg/rewrite-ns-form "(ns sort-import (:import java.util.List java.util.Date))")
  => "(ns sort-import (:import java.util.Date java.util.List))")

(fact
  "Sorts :import package prefix class names"
  (nsorg/rewrite-ns-form "(ns sort-import-prefix (:import (java.util List Date)))")
  => "(ns sort-import-prefix (:import (java.util Date List)))")

(fact
  "Sorts :require libspec :refer-macros option list (CLJS)"
  (nsorg/rewrite-ns-form "(ns sort-require-refer-macros (:require [kissa.a :refer-macros [c b a]]))")
  => "(ns sort-require-refer-macros (:require [kissa.a :refer-macros [a b c]]))")

(fact
  "Sorts :require-macros libspecs (CLJS)"
  (nsorg/rewrite-ns-form "(ns sort-require-macros (:require-macros [kissa.c :as c] kissa.b [kissa.a]))")
  => "(ns sort-require-macros (:require-macros [kissa.a] kissa.b [kissa.c :as c]))")

(fact
  "Sorts :use-macros libspecs (CLJS)"
  (nsorg/rewrite-ns-form "(ns sort-use-macros (:use-macros [kissa.c :only [x]] kissa.b [kissa.a]))")
  => "(ns sort-use-macros (:use-macros [kissa.a] kissa.b [kissa.c :only [x]]))")

(fact
  "Sorting is case-insensitive"
  (nsorg/rewrite-ns-form "(ns sort-case-insensitive (:require [kissa.a :refer [D a]]))")
  => "(ns sort-case-insensitive (:require [kissa.a :refer [a D]]))")

;; Duplicate removal rules

(fact
  "Removes duplicates from :refer-clojure :exclude option list"
  (nsorg/rewrite-ns-form "(ns dedupe-refer-clojure-exclude (:refer-clojure :exclude [map remove map]))")
  => "(ns dedupe-refer-clojure-exclude (:refer-clojure :exclude [map remove]))")

(fact
  "Removes duplicates from :refer-clojure :only option list"
  (nsorg/rewrite-ns-form "(ns dedupe-refer-clojure-only (:refer-clojure :only [map remove map]))")
  => "(ns dedupe-refer-clojure-only (:refer-clojure :only [map remove]))")

(fact
  "Removes duplicates from :refer-clojure :rename option map"
  (nsorg/rewrite-ns-form "(ns dedupe-refer-clojure-rename (:refer-clojure :rename {map foo filter foo}))")
  => "(ns dedupe-refer-clojure-rename (:refer-clojure :rename {filter foo}))")

(fact
  "Removes exact duplicates from :require libspecs"
  (nsorg/rewrite-ns-form "(ns dedupe-require (:require [kissa.c :as c] kissa.b [kissa.a] kissa.b))")
  => "(ns dedupe-require (:require [kissa.a] kissa.b [kissa.c :as c]))")

(fact
  "Removes exact duplicates from :require prefix libspecs"
  (nsorg/rewrite-ns-form "(ns dedupe-require-prefix (:require [kissa b [c :as c] a b]))")
  => "(ns dedupe-require-prefix (:require [kissa a b [c :as c]]))")

(fact
  "Removes duplicates from :require libspec :refer option list"
  (nsorg/rewrite-ns-form "(ns dedupe-require-refer (:require [kissa.a :refer [b c b]]))")
  => "(ns dedupe-require-refer (:require [kissa.a :refer [b c]]))")

(fact
  "Removes duplicates from :require prefix libspec :refer option list"
  (nsorg/rewrite-ns-form "(ns dedupe-require-prefix-refer (:require [kissa [a :refer [c a c]]]))")
  => "(ns dedupe-require-prefix-refer (:require [kissa [a :refer [a c]]]))")

(fact
  "Removes exact duplicates from :use libspecs"
  (nsorg/rewrite-ns-form "(ns dedupe-use (:use kissa.b [kissa.c :exclude [x]] [kissa.a] kissa.b))")
  => "(ns dedupe-use (:use [kissa.a] kissa.b [kissa.c :exclude [x]]))")

(fact
  "Removes exact duplicates from :use prefix libspecs"
  (nsorg/rewrite-ns-form "(ns dedupe-use-prefix (:use [kissa b [c :as c] a b]))")
  => "(ns dedupe-use-prefix (:use [kissa a b [c :as c]]))")

(fact
  "Removes duplicates from :use libspec :exclude option list"
  (nsorg/rewrite-ns-form "(ns dedupe-use-exclude (:use [kissa.a :exclude [c b c]]))")
  => "(ns dedupe-use-exclude (:use [kissa.a :exclude [b c]]))")

(fact
  "Removes dulicates from :use libspec :only option list"
  (nsorg/rewrite-ns-form "(ns dedupe-use-only (:use [kissa.a :only [c b c]]))")
  => "(ns dedupe-use-only (:use [kissa.a :only [b c]]))")

(fact
  "Removes duplicates from :use libspec :rename option map"
  (nsorg/rewrite-ns-form "(ns dedupe-use-rename (:use [kissa.a :rename {b foo c baz a foo}]))")
  => "(ns dedupe-use-rename (:use [kissa.a :rename {a foo c baz}]))")

(fact
  "Removes duplicates from :use prefix libspec :exclude option list"
  (nsorg/rewrite-ns-form "(ns dedupe-use-prefix-exclude (:use [kissa [a :exclude [c b c]]]))")
  => "(ns dedupe-use-prefix-exclude (:use [kissa [a :exclude [b c]]]))")

(fact
  "Removes duplicates from :use prefix libspec :only option list"
  (nsorg/rewrite-ns-form "(ns dedupe-use-prefix-only (:use [kissa [a :only [c b c]]]))")
  => "(ns dedupe-use-prefix-only (:use [kissa [a :only [b c]]]))")

(fact
  "Removes duplicates from :use prefix libspec :rename option map"
  (nsorg/rewrite-ns-form "(ns dedupe-use-prefix-rename (:use [kissa [a :rename {b foo c baz a foo}]]))")
  => "(ns dedupe-use-prefix-rename (:use [kissa [a :rename {a foo c baz}]]))")

(fact
  "Removes duplicates from :import class names"
  (nsorg/rewrite-ns-form "(ns dedupe-import (:import java.util.Date java.util.List java.util.Date))")
  => "(ns dedupe-import (:import java.util.Date java.util.List))")

(fact
  "Removes duplicates from :import package prefix class names"
  (nsorg/rewrite-ns-form "(ns dedupe-import-prefix (:import (java.util Date List Date)))")
  => "(ns dedupe-import-prefix (:import (java.util Date List)))")

(fact
  "Removes duplicates from :require libspec :refer-macros option list (CLJS)"
  (nsorg/rewrite-ns-form "(ns dedupe-require-refer-macros (:require [kissa.a :refer-macros [c b c]]))")
  => "(ns dedupe-require-refer-macros (:require [kissa.a :refer-macros [b c]]))")

(fact
  "Removes exact duplicates from :require-macros libspecs (CLJS)"
  (nsorg/rewrite-ns-form "(ns dedupe-require-macros (:require-macros [kissa.c :as c] kissa.b [kissa.a] kissa.b))")
  => "(ns dedupe-require-macros (:require-macros [kissa.a] kissa.b [kissa.c :as c]))")

(fact
  "Removes exact duplicates from :use-macros libspecs (CLJS)"
  (nsorg/rewrite-ns-form "(ns dedupe-use-macros (:use-macros [kissa.c :only [x]] kissa.b [kissa.a] [kissa.a]))")
  => "(ns dedupe-use-macros (:use-macros [kissa.a] kissa.b [kissa.c :only [x]]))")

;; Unused removal rules

(fact
  "unused require :refer"
  (nsorg/rewrite-ns-form
    "
  (ns foo
    (:require [clojure.string :refer [blank? starts-with?]]))

  (defn not-blank? [s]
    (not (blank? s)))")
  => "
  (ns foo
    (:require [clojure.string :refer [blank?]]))

  (defn not-blank? [s]
    (not (blank? s)))")

;; Custom rules

(fact
  "Rewrites ns form using given rules"
  (nsorg/rewrite-ns-form
    "
  (ns rewrite-using-given-rules
    ;; use
    (:use [kissa.c :only [y z x]]
          kissa.a
          [kissa.b]
    ))"
    {:rules [(nsorg/sort-ns-clause :use)]})
  => "
  (ns rewrite-using-given-rules
    ;; use
    (:use kissa.a
          [kissa.b]
          [kissa.c :only [y z x]]
    ))")

;; Other

(fact
  "Preserves formatting"
  (nsorg/rewrite-ns-form "
  (ns preserve-formatting
    ;; use
    (:use [kissa.c :only [y z x]]
          kissa.a
          [kissa.b]
    ))")
  => "
  (ns preserve-formatting
    ;; use
    (:use kissa.a
          [kissa.b]
          [kissa.c :only [x y z]]
    ))")

(fact
  "Does not modify input if ns form is not found"
  (nsorg/rewrite-ns-form "") => "")

(fact
  "Handles #_ reader macro"
  (nsorg/rewrite-ns-form "(ns reader-macro (:require #_[kissa.c :refer [z y x]] [kissa.b] [kissa.a]))")
  => "(ns reader-macro (:require [kissa.a] [kissa.b] #_[kissa.c :refer [z y x]]))")
