(ns nsorg.zip
  "Functions for working with zippers."
  (:require [rewrite-clj.zip :as zip]))

(defn right-nodes
  "Return seq of zipper nodes that are right siblings of the given zipper node.

  Parameters:
    zloc - zipper node"
  [zloc]
  (->> zloc
       (iterate zip/right)
       (take-while identity)))

(defn order-nodes
  "Order given zipper nodes alphabetically by their sexpr value. If sexpr is sequential, use the sexpr value of the
  first item for sorting.

  Parameters:
  zlocs - zipper nodes to sort"
  [zlocs]
  (sort-by (fn [zloc]
             (let [sexpr (zip/sexpr zloc)]
               (if (sequential? sexpr)
                 (first sexpr)
                 sexpr)))
           zlocs))

(defn order-node-pairs
  "Order given zipper node pairs alphabetically by sexpr value of left element of the pair. If sexpr is sequential,
  use the sexpr value of the first item for sorting.

  Parameters:
  zlocs - zipper node pairs to sort"
  [zlocs]
  (sort-by (fn [[zloc _]]
             (let [sexpr (zip/sexpr zloc)]
               (if (sequential? sexpr)
                 (first sexpr)
                 sexpr)))
           zlocs))

(defn order-sexpr
  "Order child zipper nodes of given collection zipper node.

  Parameters:
    zloc           - collection zipper node
    exclude-first? - exclude first element of collection from sorting (default: false)
    map?           - collection is map and nodes should be ordered as pairs (default: false)"
  ([zloc]
   (order-sexpr zloc nil))
  ([zloc {:keys [exclude-first? map?] :as opts}]
   (zip/up
     (let [start-zloc (cond->
                        (zip/down zloc)
                        exclude-first? (zip/right))]
       (reduce
         (fn [zloc zloc2]
           (let [replaced-zloc (zip/subedit-node zloc (fn [_] (zip/of-string (zip/string zloc2))))]
             (or (zip/right replaced-zloc) replaced-zloc)))
         start-zloc
         (let [zlocs (right-nodes start-zloc)]
           (if map?
             (mapcat identity (order-node-pairs (partition-all 2 zlocs)))
             (order-nodes zlocs))))))))

(defn organize-ns-form
  "Organize ns form by applying given rules. A single rule a map with two keys:

    :predicate - function of zipper node decided if rule should be applied to this node
    :transform - modifies given zipper node

  Parameters:
    ns-zloc - zipper node of ns form
    rules   - collection of rules to apply "
  [ns-zloc rules]
  (reduce
    (fn [zloc {:keys [predicate transform]}]
      (zip/postwalk zloc predicate transform))
    ns-zloc
    rules))

(defn find-ns-form
  "Finds ns form in the subtree of given zipper node.

  Parameters:
    zloc - zipper node"
  [zloc]
  (zip/up (zip/find-value zloc zip/next 'ns)))
