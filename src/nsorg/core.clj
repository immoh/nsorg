(ns nsorg.core
  (:require [nsorg.zip :as nzip]
            [rewrite-clj.zip :as zip]))

(defn map-option?
  "Returns true if given zipper node is a map value of given keyword option.

  Parameters:
    kw   - option keyword
    zloc - zipper node"
  [kw zloc]
  (and (map? (nzip/sexpr zloc))
       (= kw (nzip/sexpr (zip/left zloc)))))

(defn seq-option?
  "Returns true if given zipper node is a seq value of given keyword option.

  Parameters:
    kw   - option keyword
    zloc - zipper node"
  [kw zloc]
  (and (vector? (nzip/sexpr zloc))
       (= kw (nzip/sexpr (zip/left zloc)))))

(defn ns-clause?
  "Returns true if given zipper node is a ns clause.

  Parameters:
    kw   - option keyword
    zloc - zipper node"
  [kw zloc]
  (and (sequential? (nzip/sexpr zloc))
       (= kw (first (nzip/sexpr zloc)))))

(defn prefix-libspex?
  "Returns true if given zipper node is a prefix libspec.

  Parameters:
    kw   - option keyword
    zloc - zipper node"
  [kw zloc]
  (let [sexpr (nzip/sexpr zloc)
        parent-sexpr (nzip/sexpr (zip/up zloc))]
    (and (sequential? sexpr)
         (or (symbol? (second sexpr))
             (vector? (second sexpr)))
         (list? parent-sexpr)
         (= kw (first parent-sexpr)))))

(defn remove-duplicates-from-map-option
  "Create rule for removing duplicates map option values.

  Parameters:
    kw - option keyword"
  [kw]
  {:predicate (partial map-option? kw)
   :transform (fn [zloc]
                (nzip/remove-duplicates-from-sexpr zloc {:map? true}))})

(defn remove-duplicates-from-seq-option
  "Create rule for removing duplicates seq option values.

  Parameters:
    kw - option keyword"
  [kw]
  {:predicate (partial seq-option? kw)
   :transform nzip/remove-duplicates-from-sexpr})

(defn remove-duplicates-from-ns-clause
  "Create rule for removing duplicates from ns clause.

  Parameters:
    kw - ns clause type"
  [kw]
  {:predicate (partial ns-clause? kw)
   :transform (fn [zloc]
                (nzip/remove-duplicates-from-sexpr zloc {:exclude-first? true}))})

(defn remove-duplicates-from-prefix-libspec
  "Create rule for removing duplicates from prefix libspec.

  Parameters:
    kw - ns clause type"
  [kw]
  {:predicate (partial prefix-libspex? kw)
   :transform (fn [zloc]
                (nzip/remove-duplicates-from-sexpr zloc {:exclude-first? true}))})

(defn sort-map-option
  "Create rule for sorting map option values.

  Parameters:
    kw - option keyword"
  [kw]
  {:predicate (partial map-option? kw)
   :transform (fn [zloc]
                (nzip/order-sexpr zloc {:map? true}))})

(defn sort-seq-option
  "Create rule for sorting seq option values.

  Parameters:
    kw - option keyword"
  [kw]
  {:predicate (partial seq-option? kw)
   :transform nzip/order-sexpr})

(defn sort-prefix-libspec
  "Create rule for sorting prefix libspec.

  Parameters:
    kw - ns clause type"
  [kw]
  {:predicate (partial prefix-libspex? kw)
   :transform (fn [zloc]
                (nzip/order-sexpr zloc {:exclude-first? true}))})

(defn sort-ns-clause
  "Create rule for sorting ns clause.

  Parameters:
    kw - ns clause type"
  [kw]
  {:predicate (partial ns-clause? kw)
   :transform (fn [zloc]
                (nzip/order-sexpr zloc {:exclude-first? true}))})

(def default-rules
  "Default rule set."
  [(remove-duplicates-from-map-option :rename)
   (remove-duplicates-from-seq-option :exclude)
   (remove-duplicates-from-seq-option :only)
   (remove-duplicates-from-seq-option :refer)
   (remove-duplicates-from-seq-option :refer-macros)
   (remove-duplicates-from-prefix-libspec :import)
   (remove-duplicates-from-prefix-libspec :require)
   (remove-duplicates-from-prefix-libspec :use)
   (remove-duplicates-from-ns-clause :import)
   (remove-duplicates-from-ns-clause :require)
   (remove-duplicates-from-ns-clause :require-macros)
   (remove-duplicates-from-ns-clause :use)
   (remove-duplicates-from-ns-clause :use-macros)

   (sort-map-option :rename)
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
   (if-let [zloc (nzip/find-ns-form (zip/of-string s))]
     (zip/root-string (nzip/organize-ns-form zloc (:rules opts)))
     s)))
