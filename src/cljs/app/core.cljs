(ns app.core
  (:require
   [clojure.string :as string]))

; what do we want after a round?
; side effect: change score (swap!)
; maybe a map with :output-string, :winner

(def log (.-log js/console))

(def choices
  {0 "rock"
   1 "paper"
   2 "scissors"})

(def victory-strings
  {"rock" "Rock beats scissors."
   "paper" "Paper beats rock."
   "scissors" "Scissors beats paper."})

(defn get-computer-choice []
  (let [n (rand-int 3)]
    (get choices n)))

(defn play [player-selection computer-selection]
  (let [ps-lowercased (string/lower-case player-selection)
        selections (group-by :selection
                             [{:name "Computer" :selection computer-selection}
                              {:name "Player" :selection ps-lowercased}])
        get-name (comp :name first)

        return-winner (fn [selection]
                        {:output-string (str (get-name (get selections selection)) " wins. " (get victory-strings selection))
                         :winner (->> selection
                                      (get selections)
                                      get-name
                                      string/lower-case
                                      keyword)})]
    (cond
      (not (seq (#{"rock" "paper" "scissors"} ps-lowercased)))
      {:output-string "What the fuck Gordon?"
       :winner :error}

      (= (count selections) 1)
      {:output-string "Tie Game"
       :winner :tie}

      (contains? selections "rock")
      (if (contains? selections "paper")
        (return-winner "paper")
        (return-winner "rock"))

      :else
      (return-winner "scissors"))))

(play "rock" (get-computer-choice))

;TODO - update this to handle new output of play function
(defn game []
  (let [scores (atom {:computer-score 0 :player-score 0})]
    ;TODO change to a loop recur that counts how many valid games were played my mans
    (dotimes [_ 5]
      (let [selection (js/prompt "Do it gordon")
            output (play selection (get-computer-choice))]
        (case (:winner output)
          :computer (do
                      (swap! scores #(update % :computer-score inc))
                      (log (:output-string output)))
          :player (do
                    (swap! scores #(update % :player-score inc))
                    (log (:output-string output)))
          (log "Tie game"))))
    (log (clj->js @scores))))

(defn ^:export init []
  (game))
