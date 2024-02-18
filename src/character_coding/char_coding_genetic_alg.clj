(ns character-coding.char-coding-genetic-alg)

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
              ['J' 2]])

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

;; Because of latter part of genetic algorithm, we should convert array of characters and frequences to map
(defn array-to-map
  "Convert array to map"
  [array]
  (into {} array)
  )

; Initialization of population

(defn generate-chromosome
  [chromosome-size max-number-bytes]
  (vec (repeatedly chromosome-size #(inc (rand-int max-number-bytes)))))


(defn create-initial-population
  [population-size, chromosome-size]
  (let [max-number-of-bytes (get-max-number-of-bytes chromosome-size)]
    (vec (repeatedly population-size (fn [] (generate-chromosome chromosome-size max-number-of-bytes))))))

(defn generate-chromosome-optimised
  [chromosome-size max-number-bytes]
  (repeatedly chromosome-size #(inc (rand-int max-number-bytes))))
(defn create-initial-population-optimised
  [population-size, chromosome-size]
  (let [max-number-of-bytes (get-max-number-of-bytes chromosome-size)]
    (loop [i 0
           population ()]
      (if (< i population-size)
        (recur (inc i) (conj population (generate-chromosome-optimised chromosome-size max-number-of-bytes)))
        population))))

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
  [population letters]
  (filter #(< 0 (calculate-memory % letters)) population))

;;Now parents should be chosen based on their adaptability.
;; Individuals with better traits are more likely to live longer,
;; and therefore reproduce. In my case, the individual has better attributes
;; if it is represented with less memory
;;
;; The technique chosen by parents is the roulette technique.
;; Individuals with better fitness function will have a higher probability of being Parents,
;; but individuals with lower fitness function also have a chance to be parents.

(defn calculate-fitness [ind letters]
  (double (/ 1 (calculate-memory ind letters))))




;;Za sada gledamo ovo
(defn probabilities
  [population letters]
  (let [total (reduce (fn [total ind] (+ total (calculate-fitness ind letters))) 0 population)]
    ;(println total)
    (reduce (fn [result ind]
              (conj result (double
                             (/
                               (calculate-fitness ind letters)
                               total)))) [] population)))

;returns concat chromozome and probabilities
(defn set-probabilities
  [population letters]
  (let [total (reduce (fn [total ind] (+ total (calculate-fitness ind letters))) 0 population)]
    (mapv (fn [ind]
            (conj ind
                  ;(calculate-individual-fitness ind letters)
                  ;(calculate-fitness ind letters)
                  (double (/ (calculate-fitness ind letters) total)))) population)))


(defn probability
  [individual letters total]
  (double (/ (calculate-fitness individual letters) total)))

;///////////////////////
(defn cumulative-values [niz]
  (let [cumulative (reductions + 0 niz)]
    (map vector cumulative (rest cumulative))))

(defn cumulative-probabilities
  [population letters]
  (cumulative-values (probabilities population letters)))

(defn get-index-selected
  [number cumulative-probs]
  (loop [i 0]
    (if (< i (count cumulative-probs))
      (if (and (< (first (nth cumulative-probs i)) number)
               (<= number (second (nth cumulative-probs i))))
        i
        (recur (inc i)))
      nil)))

(defn roulette-wheel-selection
  [num-of-selection population letters]
  (let [cumulative-probs (cumulative-probabilities population letters)]
    (loop [i 0
           result []
           roulette-num (rand)]
      (if (< i num-of-selection)
        (let [selected-ind (nth population (get-index-selected roulette-num cumulative-probs))]
          (recur (inc i) (conj result selected-ind) (rand)))
        result))))

(defn sum-of-probabilities
  [population letters size]
  (loop [i 0
         result 0]
    (if (< i size)
      (recur (inc i) (+ result (calculate-fitness (nth population i) letters)))
      result)))
(defn cumulative-values-optimised
  [population letters]
  (let [size (count population)
        total (sum-of-probabilities population letters size)]
    (loop [i 0
           result []]
      (if (< i size)
        (let [individual (nth population i)
              probability (double (/ (calculate-fitness individual letters) total))]
          (if (= i 0)
            (recur (inc i)(conj result (conj individual probability 0 probability)))
            (recur (inc i)
                   (conj result
                         (conj individual probability (last (nth result (dec i))) (+ probability (last (nth result (dec i)))))))))
        result))))

(defn binary-search-selection
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
;; Crossover
;;In this example, I will use uniform crossover,
;; because in this way to increase the variety of solutions and to try to avoid the problem of the best local solution

(defn generate-xover-point
  [chromosome-length]
  (let [first (inc (rand-int chromosome-length))
        second (loop [num (inc (rand-int (dec chromosome-length)))]
                 (if (= num first)
                   (recur (inc (rand-int (dec chromosome-length))))
                   num))]
    (sort [first second])))

(defn prepare-for-crossover
  [ind points]
  [(subvec ind 0 (first points))
   (subvec ind (first points) (second points))
   (subvec ind (second points) (count ind))])
(defn two-point-crossover
  [parent-1 parent-2 points]
  (let [par-1 (prepare-for-crossover parent-1 points)
        par-2 (prepare-for-crossover parent-2 points)]
    [(vec (flatten [(first par-1) (second par-2) (last par-1)]))
     (vec (flatten [(first par-2) (second par-1) (last par-2)]))]))

(defn reproduce-children
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
  [population children]
  (concat population children))

(defn generate-mutation-point
  [chromosome-length]
  (let [first (inc (rand-int chromosome-length))
        second (loop [num (inc (rand-int chromosome-length))]
                 (if (= num first)
                   (recur (inc (rand-int chromosome-length)))
                   num))]
    (sort [first second])))
(defn inversion-mutation
  [ind points]
  (let [first-part (take (dec (first points)) ind)
        second-part (subvec ind (dec (first points)) (second points))
        last-part (drop (second points) ind)]
    (concat first-part (flatten (reverse second-part)) last-part)))

(defn mutate
  [population chromosome-length mutation-rate]
  (map (fn [individual]
         (if (<= (rand) mutation-rate)
           (vec (inversion-mutation individual (generate-mutation-point chromosome-length)))
           individual))  population))
(defn calculate-population-fitness
  [population letters]
  (loop [i 0
         best-fitness Integer/MAX_VALUE]
    (if (< i (count population))
      (let [individual (nth population i)
            current-fitness (calculate-memory individual letters)]
        (if (<= current-fitness best-fitness)
          (recur (inc i) current-fitness)
          (recur (inc i) best-fitness)))
      best-fitness)))
(defn calculate-optimal-individual-genetic
  [initial-population-size number-of-generations letters]

  (let [chromosome-size (count letters)
        initial-population (create-initial-population initial-population-size chromosome-size)
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
          (let [parents (roulette-wheel-selection 100 population letters)
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

(defn create-new-generation
  [population letters]
  (merge-parent-and-children population
                             (survival (reproduce-children
                                         (roulette-wheel-selection-optimised 100 population letters)
                                         (count letters)) letters)))