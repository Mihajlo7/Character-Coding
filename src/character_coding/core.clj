(ns character-coding.core
  (:require [character-coding.char-coding-brute-force :as brute-force]
            [character-coding.char-coding-genetic-alg :as genetic-alg]
            [criterium.core :refer :all]))

(def letters [['A' 5]
              ['B' 1]
              ['C' 6]
              ['D' 3]])

(defn text-to-array
  "Converting input text to array of characters and their freq"
  [text]
  (into [] (frequencies text)))

(defn create-binary-combinations
  "Creating random k binary codes of n bites "
  [n k]
  (if (<= k (Math/pow 2 n))
    (take k (for [i (range (Math/pow 2 n))]
              (apply str (reverse (map #(if (zero? (bit-and i (bit-shift-left 1 %))) "0" "1") (range n))))))
    (throw (IllegalArgumentException. "Invalid input!"))))
(defn create-binary-map
  "Creating binary codes for individuals"
  [individual]
  (let [counts (frequencies individual)]
    (reduce (fn [acc [num count]]
              (assoc acc num (create-binary-combinations num count)))
            {}
            counts)))

(defn remove-first-binary
  "Removing first element from binary group and returns rest of elements"
  [binary-map n]
  (update-in binary-map [n] rest))

(defn genetic-algorithm-code
  "Returns binary codes for genetic algorithm based on best individual and letters"
  [letters individual]
  (let [size (count letters)]
    (loop [i 0
           result {}
           binary-codes (create-binary-map individual)]
      (if (< i size)
        (recur (inc i)
               ;(conj result (conj [] (first (get letters i)) (first (get binary-codes (get individual i))) ))
               (assoc result (first (get letters i)) (first (get binary-codes (get individual i))))
               (remove-first-binary binary-codes (get individual i)))
        result))))

(defn brute-force-code
  "Returns binary codes for brute force algorithm based on best individual and letters"
  [letters individual]
  (zipmap (into [] (map first letters)) (map #(Integer/toBinaryString %) individual)))

;(def population (genetic-alg/create-initial-population 10000 (count letters)))
;(def s-population (genetic-alg/survival population letters))
;(def parent (genetic-alg/roulette-wheel-selection 100 s-population letters))
;(def children (genetic-alg/reproduce-children parent (count letters)))
;(def s-children (genetic-alg/survival children letters))
;;; MEASURES
;; Creating initial population
;;-----------------------------
;;Evaluation count : 606 in 6 samples of 101 calls.
;             Execution time mean : 1,239795 ms
;    Execution time std-deviation : 160,197692 µs
;   Execution time lower quantile : 1,060313 ms ( 2,5%)
;   Execution time upper quantile : 1,401782 ms (97,5%)
;                   Overhead used : 5,580619 ns

;; Survival population
;;--------------------
;;Evaluation count : 53664726 in 6 samples of 8944121 calls.
;             Execution time mean : 8,494737 ns
;    Execution time std-deviation : 2,340044 ns
;   Execution time lower quantile : 6,447338 ns ( 2,5%)
;   Execution time upper quantile : 11,097175 ns (97,5%)
;                   Overhead used : 5,580619 ns

;; Selection
;;-----------------
; Evaluation count : 174 in 6 samples of 29 calls.
;             Execution time mean : 3,690888 ms
;    Execution time std-deviation : 169,964530 µs
;   Execution time lower quantile : 3,568750 ms ( 2,5%)
;   Execution time upper quantile : 3,945517 ms (97,5%)
;                   Overhead used : 5,580619 ns

;; Reproduce children
;;-------------------
; Evaluation count : 558 in 6 samples of 93 calls.
;             Execution time mean : 1,381208 ms
;    Execution time std-deviation : 267,379819 µs
;   Execution time lower quantile : 1,157428 ms ( 2,5%)
;   Execution time upper quantile : 1,777840 ms (97,5%)
;                   Overhead used : 5,580619 ns

;; Merge
;;----------------
; Evaluation count : 58078446 in 6 samples of 9679741 calls.
;             Execution time mean : 6,045183 ns
;    Execution time std-deviation : 1,440402 ns
;   Execution time lower quantile : 4,758708 ns ( 2,5%)
;   Execution time upper quantile : 7,594401 ns (97,5%)
;                   Overhead used : 5,580619 ns
;; Brute force
;;------------------
;;Evaluation count : 6 in 6 samples of 1 calls.
;             Execution time mean : 7,424767 sec
;    Execution time std-deviation : 230,220428 ms
;   Execution time lower quantile : 7,230460 sec ( 2,5%)
;   Execution time upper quantile : 7,774082 sec (97,5%)
;                   Overhead used : 5,580619 ns

;;; For 25 gens
; (quick-bench (genetic-alg/create-new-generation s-population letters))
;Evaluation count : 6 in 6 samples of 1 calls.
;             Execution time mean : 2,170621 sec
;    Execution time std-deviation : 107,352769 ms
;   Execution time lower quantile : 2,035561 sec ( 2,5%)
;   Execution time upper quantile : 2,297822 sec (97,5%)
;                   Overhead used : 5,831894 ns

;; (quick-bench (genetic-alg/roulette-wheel-selection 100 s-population letters))
;Evaluation count : 6 in 6 samples of 1 calls.
;             Execution time mean : 2,167718 sec
;    Execution time std-deviation : 236,465343 ms
;   Execution time lower quantile : 1,836094 sec ( 2,5%)
;   Execution time upper quantile : 2,439353 sec (97,5%)
;                   Overhead used : 5,831894 ns
;; Optimised selection
;;Evaluation count : 30 in 6 samples of 5 calls.
;             Execution time mean : 25,545321 ms
;    Execution time std-deviation : 1,812911 ms
;   Execution time lower quantile : 23,278195 ms ( 2,5%)
;   Execution time upper quantile : 27,775337 ms (97,5%)
;                   Overhead used : 5,575167 ns