(ns character-coding.char-coding-genetic-alg
  (:require [criterium.core :refer [quick-bench]]))

;; Genetic algorithm

(def letters [['A' 5]
              ['B' 1]
              ['C' 6]
              ['D' 3]
              ['E' 2]
              ['F' 1]
              ['G' 4]
              ['H' 4]
              ['I' 2]
              ['J' 2]
              ['K' 7]
              ['L' 4]
              ['M' 2]
              ['N' 1]
              ['O' 3]
              ['P' 3]
              ['Q' 4]
              ['R' 2]
              ['T' 1]
              ['S' 5]
              ;['g' 4]
              ;['m' 7]
              ;['a' 4]
              ;['r' 4]
              ;['t' 10]
              ;['b' 3]
              ;['c' 5]
              ;['d' 4]
              ;['h' 3]
              ;['v' 4]
               ])
;; First we should create initial population for our problem


;;
;; After much consideration about the best way to encode letters into binary codes,
;; I have come to the following conclusion. The initial idea was to assign a unique decimal value to each letter;
;; for example, if there are 26 letters, each letter would be assigned a value from 1 to 26.
;; A better solution would be to use values from 0 to 25 without repetition.
;;
;; After considering this further, I've come to the conclusion that this isn't the best solution.
;; For instance, a single character can be represented in binary as 0, but it can also be represented as 00 or 000,
;; all of which have the same decimal value. This approach leads to an optimal solution because, for the fitness function,
;; we are seeking a solution that minimizes the size of the memory occupied by characters.
;; The focus is on the number of binary characters rather than their values.
;;
;; Even though this solution is superior in terms of the objective function,
;; it is more challenging to implement due to later steps in the genetic algorithm.

(defn get-max-number-of-bytes
  "Calculate max number of bytes to represent numbers 1..char-count"
  [char-count]
  (int (Math/ceil (Math/sqrt char-count)))
  )

; Initialization of population

(defn generate-chromosome
  "Generate a sequence of size n containing random numbers from 1 to the maximum number
  represented by the number of bits used to represent the value"
  [chromosome-size max-number-bytes]
  (vec (repeatedly chromosome-size #(inc (rand-int max-number-bytes)))))

(defn gen-chromosome
  [chromosome-size max-number-bytes]
  (into [] (repeatedly chromosome-size #(inc (rand-int max-number-bytes)))))

(defn c-population
  [size chromosome]
  (let [max (get-max-number-of-bytes chromosome)
        population (atom [])]
    (dotimes [i size]
      (swap! population conj (gen-chromosome chromosome max)))
    @population))
(defn create-initial-population
  "Create population of n size which contains n chromosomes"
  [population-size, chromosome-size]
  (let [max-number-of-bytes (get-max-number-of-bytes chromosome-size)]
    (into [] (repeatedly population-size (fn [] (gen-chromosome chromosome-size max-number-of-bytes))))))

;----- Optimized function------
(defn generate-chromosome-optimised
  "Optimised calculate max number of bytes to represent numbers 1..char-count"
  [chromosome-size max-number-bytes]
  (repeatedly chromosome-size #(inc (rand-int max-number-bytes))))
(defn create-initial-population-optimised
  "Optimised generate a sequence of size n containing random numbers from 1 to the maximum number
  represented by the number of bits used to represent the value"
  [population-size, chromosome-size]
  (let [max-number-of-bytes (get-max-number-of-bytes chromosome-size)]
    (loop [i 0
           population (transient [])]
      (if (< i population-size)
        (recur (inc i) (conj! population (generate-chromosome-optimised chromosome-size max-number-of-bytes)))
        (persistent! population)))))

;;The next step is to calculate the adaptation measure.
;; It is obtained by calculating the fitness function for each individual,
;; and then observing whether the individual can be in the population
;;
;;We will observe the correctness of the individual in the number of coding combinations.
;; For example, only two characters can be replaced with one bit value, because a character can
;; take the value 0 or 1. If more than 2 genes containing the value 1 appear in an individual,
;; we will declare it incorrect. Based on this logic,
;; we define the condition of non-acceptance of an individual:
;;
;; count(c)<=2^c
;;
;; where c is number of bytes to represent character
;;
;; In order to optimize the solution, we will sort each gene of each individual in ascending order

(defn calculate-memory
  "Calculating fitness function based on count of bytes for one individual"
  [individual letters]
  (let [freq (frequencies individual)]
    (if (every? #(<= (val %) (Math/pow 2 (key %))) freq)
      (reduce + (map * individual (vec (map second letters))))
      0)))


(defn survival
  "Select only those individuals that satisfy the problem constraint"
  [population letters]
  (filter #(< 0 (calculate-memory % letters)) population))

;; ---------- SELECTION -------------------
;;Now parents should be chosen based on their adaptability.
;; Individuals with better traits are more likely to live longer,
;; and therefore reproduce. In my case, the individual has better attributes
;; if it is represented with less memory
;;
;; The technique chosen by parents is the roulette technique.
;; Individuals with better fitness function will have a higher probability of being Parents,
;; but individuals with lower fitness function also have a chance to be parents.

(defn calculate-fitness [ind letters]
  "Calculates the adapted fitness function for easier computation of probabilities and cumulative probabilities"
  (double (/ 1 (calculate-memory ind letters))))


(defn probabilities
  "Calculates the probability based on the fitness function by dividing the applied objective function
  for a single continuous individual by the total applied fitness function"
  [population letters]
  (let [total (reduce (fn [total ind] (+ total (calculate-fitness ind letters))) 0 population)]
    ;(println total)
    (reduce (fn [result ind]
              (conj result (double
                             (/
                               (calculate-fitness ind letters)
                               total)))) [] population)))

(defn cumulative-values
  "Calculates the cumulative values based on elements of array"
  [array]
  (let [cumulative (reductions + 0 array)]
    (map vector cumulative (rest cumulative))))

(defn cumulative-probabilities
  "Calculates the cumulative probability for all individuals"
  [population letters]
  (cumulative-values (probabilities population letters)))

(defn get-index-selected
  "Returns the index of the individual such that a randomly
  generated code falls within the cumulative value range for that individual"
  [number cumulative-probs]
  (loop [i 0]
    (if (< i (count cumulative-probs))
      (if (and (< (first (nth cumulative-probs i)) number)
               (<= number (second (nth cumulative-probs i))))
        i
        (recur (inc i)))
      nil)))

(defn roulette-wheel-selection
  "Returns n individuals who will be parents based on randomly selected individuals using the roulette wheel selection method"
  [num-of-selection population letters]
  (let [cumulative-probs (cumulative-probabilities population letters)]
    (loop [i 0
           result []
           roulette-num (rand)]
      (if (< i num-of-selection)
        (let [selected-ind (nth population (get-index-selected roulette-num cumulative-probs))]
          (recur (inc i) (conj result selected-ind) (rand)))
        result))))

;;; ---- Optimised selection
(defn sum-of-fitness
  "Returns sum of fitness for population"
  [population letters size]
  (loop [i 0
         result 0]
    (if (< i size)
      (recur (inc i) (+ result (calculate-fitness (nth population i) letters)))
      result)))
(defn sum-of-fitness-2
  [population letters ]
  (reduce (fn [res ind]
            (+ res (calculate-fitness ind letters))) 0 population))
(defn sum-of-fitness-3
  [population letters]
  (let [a (map #(calculate-fitness % letters) population)
        array  (double-array a )]
    (areduce ^doubles array i result (double 0.0) (+ result (aget  ^doubles array i)))))
(defn fitness-array
  [population letters]
  (let [total (sum-of-fitness-2 population letters )]
    (reduce (fn [res ind] (conj res (/ (double (calculate-fitness ind letters)) total) )) [] population)))


(defn cumulative-array
  [fitness-array]
  (reductions + fitness-array))

(defn binary-search-opt
  [number cumulative-values ]
  (if (<= number (nth cumulative-values 0))
    0
    (loop [low 0
           high (dec (count cumulative-values))]
      (if (<= low high)
        (let [mid (quot (+ low high) 2)
              mid-value (nth cumulative-values mid)]
          (if (<= number mid-value)
            (if (> number (nth cumulative-values (dec mid)))
              mid
              (recur low (- mid 1)))
            (recur (+ mid 1) high)))
        low))))

(defn cumulative-values-optimised
  "Optimised calculates the cumulative probability for all individuals"
  [population letters]
  (let [size (count population)
        total (sum-of-fitness population letters size)]
    (loop [i 0
           result (transient [])]
      (if (< i size)
        (let [individual (vec (nth population i))
              probability (double (/ (calculate-fitness individual letters) total))]
          (if (= i 0)
            (recur (inc i)(conj! result (conj individual 0 probability)))
            (recur (inc i)
                   (conj! result
                         (conj individual (last (nth result (dec i))) (+ probability (last (nth result (dec i)))))))))
        (persistent! result)))))



(defn binary-search-selection
  "Implementation algorithm for binary search of array"
  [rand-number population]
  (loop [low 0
         high (dec (count population))]
    (if (<= low high)
      (let [mid (quot (+ low high) 2)
            mid-value (nth population mid)]
        (if (<= rand-number (last mid-value))
          (if (> rand-number (last (butlast mid-value)))
            mid-value
            (recur low (- mid 1)))
          (recur (+ mid 1) high)))
      (last population))))
(defn roulette-wheel-selection-optimised
  "Optimised selection based on roulette wheel, uses better perfomance and binary search"
  [num-of-selection population letters]
  (let [population-with-prob (cumulative-values-optimised population letters)
        size (count letters)]
    (loop [i 0
           result []
           generated-number (rand)]
      (if (< i num-of-selection)
        (recur (inc i)
               (conj result (vec (take size (binary-search-selection generated-number population-with-prob))))
               (rand))
        result))))

;; Ranking selection
(defn ranking-selection
  [number-of-selection population]
  (take number-of-selection population))

;; Tournament selection

(defn select-n-individual
  [population letters n]
  (let [result (transient [])]
    (dotimes [i n]
      (conj! result (rand-nth population)))
    (persistent! result)))

(defn calculate-population-fitness
  "Returns the best value of the fitness function for the entire population and the individual
  associated with that fitness function"
  [population letters]
  (loop [i 0
         best-fitness Integer/MAX_VALUE
         best-individual []]
    (if (< i (count population))
      (let [individual (nth population i)
            current-fitness (calculate-memory individual letters)]
        (if (<= current-fitness best-fitness)
          (recur (inc i) current-fitness individual)
          (recur (inc i) best-fitness best-individual)))
      {:memory best-fitness :individual best-individual})))

(defn select-n-individual-optimised
  [population n]
  (let [population-size (count population)
        random-indices (repeatedly n #(rand-int population-size))
        selected-individuals (map #(nth population %) random-indices)]
    selected-individuals))

(defn tournament-selection
  [population letters n]
  (let [groups (partition n population)
        bests (map #(calculate-population-fitness % letters) groups) ]
    (mapv :individual bests)))

;;;----------- REPRODUCE CHILDREN ---------------

;; Crossover
;;In this example, I will use uniform crossover,
;; because in this way to increase the variety of solutions and to try to avoid the problem of the best local solution

(defn generate-xover-point
  "Generates sequnces with 2 random number from 1 to chromosome-length-1"
  [chromosome-length]
  (let [first (inc (rand-int chromosome-length))
        second (loop [num (inc (rand-int (dec chromosome-length)))]
                 (if (= num first)
                   (recur (inc (rand-int (dec chromosome-length))))
                   num))]
    (sort [first second])))

(defn prepare-for-crossover
  "Divides one individual into subsequences based on the input array"
  [ind points]
  [(subvec ind 0 (first points))
   (subvec ind (first points) (second points))
   (subvec ind (second points) (count ind))])
(defn two-point-crossover
  "Generates pairs of children for the input parents by recombining gene sequences"
  [parent-1 parent-2 points]
  (let [par-1 (prepare-for-crossover (vec parent-1)  points)
        par-2 (prepare-for-crossover (vec parent-2)  points)]
    [(vec (flatten [(first par-1) (second par-2) (last par-1)]))
     (vec (flatten [(first par-2) (second par-1) (last par-2)]))]))

(defn reproduce-children
  "Generates children for the population based on a defined crossover method"
  [population chromosome-length]
  (loop [i 0
         children []
         points (generate-xover-point chromosome-length)]
    (if (< i (dec (count population)))
      (recur (inc i)
             (let [res (two-point-crossover (nth population i) (nth population (inc i)) points)]
               (conj children (first res) (second res)))
             (generate-xover-point chromosome-length))children)))
(defn merge-parent-and-children
  "Merges the population with the children and prepares it for the next generation"
  [population children]
  (concat population children))

;;---------- MUTATION -----------

(defn generate-mutation-point
  "Generates pairs for mutation"
  [chromosome-length]
  (let [first (inc (rand-int chromosome-length))
        second (loop [num (inc (rand-int chromosome-length))]
                 (if (= num first)
                   (recur (inc (rand-int chromosome-length)))
                   num))]
    (sort [first second])))
(defn inversion-mutation
  "Returns an individual with parts of the gene sequence inverted, resulting in a mutated individual"
  [ind points]
  (let [first-part (take (dec (first points)) ind)
        second-part (subvec ind (dec (first points)) (second points))
        last-part (drop (second points) ind)]
    (concat first-part (flatten (reverse second-part)) last-part)))

(defn reverse-mutation
  "Return individual with reversed sequences of genes"
  [individual]
  (reverse individual))

(defn mutate
  "Mutates certain individuals in the population.
  Whether an individual will be mutated depends on whether a randomly generated number in mutation rate"
  [population chromosome-length mutation-rate]
  (map (fn [individual]
         (if (<= (rand) mutation-rate)
           (vec (inversion-mutation individual (generate-mutation-point chromosome-length)))
           ;(vec (reverse-mutation individual))
           individual))  population))

;;; --------------- ALGORITHM ----------------


(defn create-new-generation
  "Returns a generation by performing selection, reproduction, and survival on the population"
  [population letters]
  (merge-parent-and-children population
                             (survival (reproduce-children
                                         (roulette-wheel-selection-optimised 100 population letters)
                                         (count letters)) letters)))

(defn merge-population-with-memory
  [population letters]
  (sort-by #(last %)
           (mapv (fn [ind] (conj ind (calculate-memory ind letters))) population)))
(defn create-new-generation-with-mutation
  "Returns a generation by performing selection, reproduction, mutation and survival on the population"
  [population letters mutation-rate num-of-selection]
  (let [chromosome-length (count letters)]
    (merge-parent-and-children population
                               (survival (mutate
                                           (reproduce-children
                                             (roulette-wheel-selection-optimised num-of-selection population letters)
                                             chromosome-length) chromosome-length mutation-rate) letters))))

(defn tournament-new-generation
  [population letters mutation-rate num-of-selection k]
  (let [chromosome-length (count letters)]
    (survival (mutate
                (reproduce-children
                  (tournament-selection (select-n-individual-optimised population num-of-selection) letters k)
                  chromosome-length) chromosome-length mutation-rate) letters)))

(defn genetic-algorithm-optimised
  [population-size number-of-generation num-of-selection k mutation-rate letters]
  (let [chromosome-size (count letters)
        initial-population (create-initial-population-optimised population-size chromosome-size)
        survival-population (survival initial-population letters)]
    (loop [iteration 1
           global-best (calculate-population-fitness survival-population letters)
           population survival-population]
      (if (< iteration number-of-generation)
        (let [children (tournament-new-generation population letters mutation-rate num-of-selection k)
              children-best (calculate-population-fitness children letters)]
          (if (<= (get children-best :memory) (get global-best :memory))
            (recur (+ iteration 1) children-best (merge-parent-and-children population children))
            (recur (+ iteration 1) global-best (merge-parent-and-children population children))))
        global-best))))
(defn genetic-algorithm
  "The function initiates a genetic algorithm and finds the best individual based on the initial population size,
  the number of generations, the number of parent selections, the mutation rate,
  and the characters along with their frequencies"
  [population-size number-of-generation num-of-selection mutation-rate letters]
  (let [chromosome-size (count letters)
        initial-population (create-initial-population-optimised population-size chromosome-size)
        survival-population (survival initial-population letters)]
    (loop [iteration 1
           global-best Integer/MAX_VALUE
           population survival-population
           global-best-individual []]
      (if (<= iteration number-of-generation)
        (let [current-best (calculate-population-fitness population letters)
              current-best-memory (get current-best :memory)
              current-best-individual (get current-best :individual)
              current-generation (create-new-generation-with-mutation population letters mutation-rate num-of-selection)
              next-gen current-generation]
          (println "Generation: " iteration)
          (println "Current best: " current-best-memory)
          (println "Global best: " global-best)
          (println "Population size: " (count current-generation))
          (println "-----------------\n")
          (if (<= current-best-memory global-best)
            (recur (inc iteration) current-best-memory next-gen current-best-individual)
            (recur (inc iteration) global-best next-gen global-best-individual)))
        [global-best global-best-individual]))))

;;;; --------------- DEPRICATED ---------------
(defn calculate-optimal-individual-genetic
  [initial-population-size number-of-generations letters]

  (let [chromosome-size (count letters)
        initial-population (create-initial-population-optimised initial-population-size chromosome-size)
        survival-population (survival initial-population letters)]
    (println "Initial population size: " initial-population-size)
    (println "Survived population size: " (count survival-population))
    (println "Percentage of survivors: " (/ (* (count survival-population) 100) initial-population-size) "%")
    (println "-----------------------------")
    (loop [iteration 1
           global-best Integer/MAX_VALUE
           population survival-population]
      (if (<= iteration number-of-generations)
        (let [current-best (calculate-population-fitness population letters)]
          (println "Generation: " iteration)
          (println "Global best fitness: " global-best)
          (println "Current best fitness: "current-best)
          (println "Generation size: " (count population))
          (println "****")
          (let [parents (roulette-wheel-selection-optimised 100 population letters)
                children (reproduce-children parents chromosome-size)
                survival-children (survival children letters)
                new-population (merge-parent-and-children population survival-children)]
            (println "Number of children: " (count children))
            (println "Number of survived children: " (count survival-children))
            (println "----------------\n")
            (if (<= current-best global-best)
              (recur (inc iteration) current-best new-population)
              (recur (inc iteration) global-best  new-population)))
          )
        global-best))))