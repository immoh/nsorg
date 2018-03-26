(ns nsorg.core
  (:require [nsorg.zip :as nzip]
            [rewrite-clj.zip :as zip]))

(defn sort-map-option
  "Create rule for sorting map option values.

  Parameters:
    kw - option keyword"
  [kw]
  {:predicate (fn [zloc]
                (and (map? (zip/sexpr zloc))
                     (= kw (zip/sexpr (zip/left zloc)))))
   :transform (fn [zloc]
                (nzip/order-sexpr zloc {:map? true}))})

(defn sort-seq-option
  "Create rule for sorting seq option values.

  Parameters:
    kw - option keyword"
  [kw]
  {:predicate (fn [zloc]
                (and (vector? (zip/sexpr zloc))
                     (= kw (zip/sexpr (zip/left zloc)))))
   :transform nzip/order-sexpr})

(defn sort-prefix-libspec
  "Create rule for sorting prefix libspec.

  Parameters:
    kw - ns clause type"
  [kw]
  {:predicate (fn [zloc]
                (let [sexpr (zip/sexpr zloc)
                      parent-sexpr (zip/sexpr (zip/up zloc))]
                  (and (sequential? sexpr)
                       (or (symbol? (second sexpr))
                           (vector? (second sexpr)))
                       (list? parent-sexpr)
                       (= kw (first parent-sexpr)))))
   :transform  (fn [zloc]
                 (nzip/order-sexpr zloc {:exclude-first? true}))})

(defn sort-ns-clause
  "Create rule for sorting ns clause.

  Parameters:
    kw - ns clause type"
  [kw]
  {:predicate (fn [zloc]
                (and (sequential? (zip/sexpr zloc))
                     (= kw (first (zip/sexpr zloc)))))
   :transform (fn [zloc]
                (nzip/order-sexpr zloc {:exclude-first? true}))})

(def default-rules
  "Default rule set."
  [(sort-map-option :rename)
   (sort-seq-option :exclude)
   (sort-seq-option :only)
   (sort-seq-option :refer)
   (sort-seq-option :refer-macros)
   (sort-prefix-libspec :import)
   (sort-prefix-libspec :require)
   (sort-prefix-libspec :use)
   (sort-ns-clause :import)
   (sort-ns-clause :require)
   (sort-ns-clause :require-macros)
   (sort-ns-clause :use)
   (sort-ns-clause :use-macros)])

(defn rewrite-ns-form
  "Rewrites ns form in the Clojure code given as a string using the given set of rules.
   By default applies rules that sort ns clauses, prefix libspecs and option values alphabetically.
   Preserve original whitespace and comments.

  Parameters:
    s     - string containing Clojure code
    rules - collection rules to apply "
  ([s]
    (rewrite-ns-form s {:rules default-rules}))
  ([s opts]
   (-> s
       (zip/of-string)
       (nzip/find-ns-form)
       (nzip/organize-ns-form (:rules opts))
       (zip/root-string))))
