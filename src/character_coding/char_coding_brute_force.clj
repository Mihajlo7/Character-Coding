(ns character-coding.char-coding-brute-force
  (:require [clojure.math.combinatorics :as comb]))

; Add some input character and their frequences
(def letters [['A' 5]
              ['B' 1]
              ['C' 6]
              ['D' 3]])


; If we use the standard method of storing characters, each character is represented as an 8-bit value,
; and since there are a total of 15 letters, this text occupies 128 bits.
; We will try to find an algorithm so that each character represents a binary value, thus reducing the memory size needed for storage

(defn get-all-combinations [letters]
  "Computing all combinations. This is exhaustive and computationally expensive!"
  (let [only-letter (map first letters)]
    (mapv (fn [permutation]
            (mapv (fn [char value]
                    [char value]) only-letter permutation)) (comb/permutations (range (count only-letter))))))

(defn get-all-combinations-complete [letters]
  (let [letter (map first letters)
        freq (map second letters)]
    (mapv (fn [permutation]
            (mapv (fn [char freq value]
                    [char freq value]) letter freq permutation)) (comb/permutations (range (count letter))))))
;;
;; For this algorithm, we will define a fitness function,
;; which is to find the smallest size of memory occupied by the given letters.
;; We will observe the frequency of each letter and construct the algorithm in such a way that
;; we need to assign a smaller binary value to characters that appear more frequently.

(defn calculate-row-memory [row]
  "Calculating memory size for one row "
  (reduce (fn [current,column]
            (+ current
               (* (second column)
                  (count (Integer/toBinaryString (last column)))))) 0 row)
  )
(defn calculate-fitness [letters]

  (reduce (fn [result, column]
            (let [current-memory (calculate-row-memory column)]
              (println "iteration: " (get result :i))
              (println "Best memory-size: " (get result :min-memory))
              (println "Current memory size: " current-memory)
              (if (< current-memory (get result :min-memory))
                (assoc result :min-memory current-memory)
                (assoc result :best-individuals column))
              (assoc result :i (inc (get result :i)))
              (println "----------------------------------")))
          {:min-memory Integer/MAX_VALUE
           :best-individuals []
           :i 1
           } (get-all-combinations-complete letters)))
;----------------------------
(defn calculate-fitness-1 [letters]
  (reduce (fn [result column]
            (let [current-memory (calculate-row-memory column)
                  updated-result (if (< current-memory (get result :min-memory))
                                   (assoc result :min-memory current-memory :best-individuals column)
                                   result)]
              (println "iteration: " (:i updated-result))
              (println "Best memory-size: " (:min-memory updated-result))
              (println "Current memory size: " current-memory)
              (println "----------------------------------")
              (assoc updated-result :i (inc (:i updated-result)))))
          {:min-memory Integer/MAX_VALUE
           :best-individuals []
           :i 1
           } (get-all-combinations-complete letters)))
;-------------------------------






