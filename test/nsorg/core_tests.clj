(ns nsorg.core-tests
  (:require [midje.sweet :refer :all]
            [nsorg.core :as nsorg]))
(fact
  "Sorts :refer-clojure :exclude option list"
  (nsorg/rewrite-ns-form "(ns foo (:refer-clojure :exclude [map remove filter]))")
  => "(ns foo (:refer-clojure :exclude [filter map remove]))")

(fact
  "Sorts :refer-clojure :only option list"
  (nsorg/rewrite-ns-form "(ns foo (:refer-clojure :only [remove map filter]))")
  => "(ns foo (:refer-clojure :only [filter map remove]))")

(fact
  "Sorts :refer-clojure :rename option map"
  (nsorg/rewrite-ns-form "(ns foo (:refer-clojure :rename {map m remove r filter f}))")
  => "(ns foo (:refer-clojure :rename {filter f map m remove r}))")

(fact
  "Sorts :require libspecs"
  (nsorg/rewrite-ns-form "(ns foo (:require [a.c :as c] a.b [a.a]))")
  => "(ns foo (:require [a.a] a.b [a.c :as c]))")

(fact
  "Sorts :require prefix libspecs"
  (nsorg/rewrite-ns-form "(ns foo (:require [x b [c :as c] a]))")
  => "(ns foo (:require [x a b [c :as c]]))")

(fact
  "Sorts :require libspec :refer option list"
  (nsorg/rewrite-ns-form "(ns foo (:require [a.a :refer [b c a]]))")
  => "(ns foo (:require [a.a :refer [a b c]]))")

(fact
  "Sorts :require prefix libspec :refer option list"
  (nsorg/rewrite-ns-form "(ns foo (:require [x [y :refer [c a b]]]))")
  => "(ns foo (:require [x [y :refer [a b c]]]))")

(fact
  "Sorts :use libspecs"
  (nsorg/rewrite-ns-form "(ns foo (:use a.b [a.c :exclude [d]] [a.a]))")
  => "(ns foo (:use [a.a] a.b [a.c :exclude [d]]))")

(fact
  "Sorts :use prefix libspecs"
  (nsorg/rewrite-ns-form "(ns foo (:use [x b [c :as c] a]))")
  => "(ns foo (:use [x a b [c :as c]]))")

(fact
  "Sorts :use libspec :exclude option list"
  (nsorg/rewrite-ns-form "(ns foo (:use [a.a :exclude [c b a]]))")
  => "(ns foo (:use [a.a :exclude [a b c]]))")

(fact
  "Sorts :use libspec :only option list"
  (nsorg/rewrite-ns-form "(ns foo (:use [a.a :only [c b a]]))")
  => "(ns foo (:use [a.a :only [a b c]]))")

(fact
  "Sorts :use libspec :rename option map"
  (nsorg/rewrite-ns-form "(ns foo (:use [a.a :rename {b bar c baz a foo}]))")
  => "(ns foo (:use [a.a :rename {a foo b bar c baz}]))")

(fact
  "Sorts :use prefix libspec :exclude option list"
  (nsorg/rewrite-ns-form "(ns foo (:use [a [a :exclude [c b a]]]))")
  => "(ns foo (:use [a [a :exclude [a b c]]]))")

(fact
  "Sorts :use prefix libspec :only option list"
  (nsorg/rewrite-ns-form "(ns foo (:use [a [a :only [c b a]]]))")
  => "(ns foo (:use [a [a :only [a b c]]]))")

(fact
  "Sorts :use prefix libspec :rename option map"
  (nsorg/rewrite-ns-form "(ns foo (:use [a [a :rename {b bar c baz a foo}]]))")
  => "(ns foo (:use [a [a :rename {a foo b bar c baz}]]))")

(fact
  "Sorts :import class names"
  (nsorg/rewrite-ns-form "(ns foo (:import java.util.List java.util.Date))")
  => "(ns foo (:import java.util.Date java.util.List))")

(fact
  "Sorts :import package prefix class names"
  (nsorg/rewrite-ns-form "(ns foo (:import (java.util List Date)))")
  => "(ns foo (:import (java.util Date List)))")

(fact
  "Sorts :require libspec :refer-macros option list (CLJS)"
  (nsorg/rewrite-ns-form "(ns foo (:require [a.a :refer-macros [c b a]]))")
  => "(ns foo (:require [a.a :refer-macros [a b c]]))")

(fact
  "Sorts :require-macros libspecs (CLJS)"
  (nsorg/rewrite-ns-form "(ns foo (:require-macros [a.c :as c] a.b [a.a]))")
  => "(ns foo (:require-macros [a.a] a.b [a.c :as c]))")

(fact
  "Sorts :use-macros libspecs (CLJS)"
  (nsorg/rewrite-ns-form "(ns foo (:use-macros [a.c :only [x]] a.b [a.a]))")
  => "(ns foo (:use-macros [a.a] a.b [a.c :only [x]]))")

(fact
  "Preserves formatting"
  (nsorg/rewrite-ns-form "
  (ns foo
    ;; use
    (:use [a.c :only [y z x]]
          a
          [a.b]
    ))")
  => "
  (ns foo
    ;; use
    (:use a
          [a.b]
          [a.c :only [x y z]]
    ))")

(fact
  "Rewrites ns form using given rules"
  (nsorg/rewrite-ns-form
    "
  (ns foo
    ;; use
    (:use [a.c :only [y z x]]
          a
          [a.b]
    ))"
    {:rules [(nsorg/sort-ns-clause :use)]})
  => "
  (ns foo
    ;; use
    (:use a
          [a.b]
          [a.c :only [y z x]]
    ))")
