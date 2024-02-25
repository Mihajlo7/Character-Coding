(ns character-coding.brute-force-test
  (:require [midje.sweet :refer :all]
            [character-coding.char-coding-brute-force :refer :all]
            [character-coding.core :refer :all]))

(facts "Testing function for generating all combinations"
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]]]
         (fact "Testing count combinations"
               (count (generate-all-comb letters)) => 6)
         (fact "Testing all combinations"
               (generate-all-comb letters)=> [[0 1 2]
                                              [0 2 1]
                                              [1 0 2]
                                              [1 2 0]
                                              [2 0 1]
                                              [2 1 0]])))

(facts "Testing calculating fitness"
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]
                      ['D' 3]]]
         (fact "Test case 1"
               (calculate-individual-fitness [0 1 2 3] letters)=> 24)
         (fact "Test case 2"
               (calculate-individual-fitness [1 3 0 2] letters)=> 19)
         (fact "Test case 3"
               (calculate-individual-fitness [] letters)=> 0))
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]
                      ['D' 3]
                      ['F' 1]
                      ['E' 4]
                      ['G' 2]
                      ['H' 3]]]
         (fact "Test case 4"
               (calculate-individual-fitness [0 1 2 3 4 5 6 7] letters)=> 54)
         (fact "Test case 5"
               (calculate-individual-fitness [7 6 5 4 3 2 1 0] letters)=> 60)))

(facts "Testing finding the best individual"
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]
                      ['D' 3]]
             combs (generate-all-comb letters)]
         (fact "Test case 1"
               (calculate-optimal-individual combs letters)=> [1 3 0 2])))

;-----------------------------
;----- Tests for core --------

(facts "Testing string to array"
       (let [text "ABRACADABRA"]
         (fact "Testing function"
               (text-to-array text)=> [[\A 5] [\B 2] [\R 2] [\C 1] [\D 1]])
         (fact "Test case 2"
               (count (text-to-array text))=> (count (distinct text)))
         (fact "Testing count fo characters"
               (reduce #(+ %1 (second %2)) 0 (text-to-array text))=> (count text)))
       (fact "Empty string"
             (text-to-array "")=> [])
       (fact "Special test"
             (text-to-array " . ")=> [[\space 2] [\. 1]])
       (fact "Special character"
             (text-to-array "\n")=> [[\newline 1]])
       (fact "Special test 2"
             (text-to-array "\"M\"")=> [[\" 2] [\M 1]]))

