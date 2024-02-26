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
               (generate-all-comb letters) => [[0 1 2]
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
               (calculate-individual-fitness [0 1 2 3] letters) => 24)
         (fact "Test case 2"
               (calculate-individual-fitness [1 3 0 2] letters) => 19)
         (fact "Test case 3"
               (calculate-individual-fitness [] letters) => 0))
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]
                      ['D' 3]
                      ['F' 1]
                      ['E' 4]
                      ['G' 2]
                      ['H' 3]]]
         (fact "Test case 4"
               (calculate-individual-fitness [0 1 2 3 4 5 6 7] letters) => 54)
         (fact "Test case 5"
               (calculate-individual-fitness [7 6 5 4 3 2 1 0] letters) => 60)))

(facts "Testing finding the best individual"
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]
                      ['D' 3]]
             combs (generate-all-comb letters)]
         (fact "Test case 1"
               (calculate-optimal-individual combs letters) => [1 3 0 2])))

;-----------------------------
;----- Tests for core --------

(facts "Testing string to array"
       (let [text "ABRACADABRA"]
         (fact "Testing function"
               (text-to-array text) => [[\A 5] [\B 2] [\R 2] [\C 1] [\D 1]])
         (fact "Test case 2"
               (count (text-to-array text)) => (count (distinct text)))
         (fact "Testing count fo characters"
               (reduce #(+ %1 (second %2)) 0 (text-to-array text)) => (count text)))
       (fact "Empty string"
             (text-to-array "") => [])
       (fact "Special test"
             (text-to-array " . ") => [[\space 2] [\. 1]])
       (fact "Special character"
             (text-to-array "\n") => [[\newline 1]])
       (fact "Special test 2"
             (text-to-array "\"M\"") => [[\" 2] [\M 1]]))

(facts "Testing generating binary codes"
       (fact "Standard tests"
             (create-binary-combinations 1 2) => '("0" "1")
             (create-binary-combinations 2 1) => '("00")
             (create-binary-combinations 2 2) => '("00" "01"))
       (fact "Tests for k=0"
             (create-binary-combinations 2 0)=> '()
             (create-binary-combinations 5 0)=> '())
       (fact "Exception tests"
             (create-binary-combinations 1 3)=> (throws java.lang.IllegalArgumentException)
             (create-binary-combinations 2 9)=> (throws java.lang.IllegalArgumentException)
             (create-binary-combinations 3 9)=> (throws java.lang.IllegalArgumentException)))

(facts "Testing creating binary combinations"
       (fact "Standard tests"
             (create-binary-map [1 2 1 2 2])=> {1 '("0" "1") 2 '("00" "01" "10")}
             (create-binary-map [1 2 2 2])=> {1 '("0") 2 '("00" "01" "10")}
             (create-binary-map [2 2 2 2])=> {2 '("00" "01" "10" "11")}
             (create-binary-map [1 2 3 2 2 3])=> {1 '("0") 2 '("00" "01" "10") 3 '("000" "001")}
             (create-binary-map [5 5 5 5 5])=> {5 '("00000" "00001" "00010" "00011" "00100")}))

(facts "Testing removing first binary code"
       (fact "Standard tests"
             (remove-first-binary{1 '("0" "1") 2 '("00" "01" "10")} 1 )=> {1 '("1") 2 '("00" "01" "10")}
             (remove-first-binary {1 '("0") 2 '("00" "01" "10")} 1)=> { 1 '() 2 '("00" "01" "10")}
             (remove-first-binary {2 '("00" "01" "10" "11")} 2 )=> {2 '("01" "10" "11")}
             (remove-first-binary {2 '("00" "01" "10" "11")} 1 )=> { 1 '() 2 '("00" "01" "10" "11")}
             (remove-first-binary {1 '("0") 2 '("00" "01" "10") 3 '("000" "001")} 2)=> {1 '("0") 2 '("01" "10") 3 '("000" "001")}))

(facts "Testing brute force coding"
       (let [text "ARTIFICIALINTELLIGENCE"
             letters (text-to-array text)
             individual [7 6 3 9 8 1 0 5 4 2]]
         (fact "Standard test"
               (brute-force-code letters individual)=> {\A "111" \C "110" \E "11" \F "1001" \G "1000"
                                                        \I "1" \L "0" \N "101" \R "100" \T "10"})))

(facts "Testing genetic algorithm coding"
       (let [text "ARTIFICIALINTELLIGENCE"
             letters (text-to-array text)
             individual [2 2 1 3 3 1 2 2 3 3]]
         (fact "Standard test"
               (genetic-algorithm-code letters individual)=> {\A "00" \C "01" \E "0" \F "000" \G "001"
                                                        \I "1" \L "10" \N "11" \R "010" \T "011"})))

