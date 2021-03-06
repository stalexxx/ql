(ns ql.method
  (:require [clojure.string :as str]))

(defn dispatch-sql [x]
  (cond (map? x) (get x :ql/type)
        (vector? x) (first x)
        :else (type x)))

(defmulti to-sql (fn [{sql :sql params :params} x] (dispatch-sql x)))

(defn conj-sql [acc & sql]
  (update acc :sql (fn [x] (apply conj x sql))))

(defn conj-param [acc v]
  (update acc :params conj v))

(defn reduce-separated [sep f acc coll]
  (loop [acc acc
         [x & xs] coll]
    (if (nil? xs)
      (f acc x)
      (recur (update (f acc x) :sql conj sep) xs))))

(defn comma-separated [ks]
  (->> ks
       (mapv (fn [x] (if (keyword? x) (name x) (str x))))
       (str/join ",")))

;; (comma-separated [:a :b 0])

(defn operator-args [opts]
  (if (map? opts)
    [(:ql/type opts)
     (or (:left opts) (get opts 0))
     (or (:right opts) (get opts 1))]
    opts))


