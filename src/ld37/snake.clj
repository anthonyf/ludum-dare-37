(ns ld37.snake)

(declare spawn-food move-forward)

(defn setup-game
  [& {:keys [width height] :or {width 16 height 12}}]
  (let [[start-x start-y :as start-xy] [(int (/ width 2))
                                        (int (/ height 2))]
        hairball-delay 15]
    (-> {:width width
         :height height
         :state :playing
         :direction :left
         :snake (list start-xy
                      [(+ 1 start-x) start-y]
                      [(+ 2 start-x) start-y])
         :food nil
         :hairball-delay hairball-delay
         :hairball-countdown (+ hairball-delay (rand-int hairball-delay))
         :hairballs #{}
         :grow-by 2
         :grow 0}
        spawn-food)))

#_ (setup-game)
;; => {:width 16, :height 12, :state :playing, :direction :left, :snake ([8 6] [9 6] [10 6]), :food [1 11]}

(defn empty-place?
  [{:keys [food snake hairballs]}
   [x y :as place]]
  (let [snake-positions (set snake)]
    (or (contains? food place)
        (contains? snake-positions place)
        (contains? hairballs place))))

(defn- spawn-food
  [{:keys [width height food]
    :as game}]
  (if food
    game ;; we already have food
    (loop []
      (let [food-pos [(rand-int width) (rand-int height)]]
        (if (empty-place? game food-pos)
          (recur)
          (assoc game :food food-pos))))))

#_ (-> (setup-game)
       (spawn-food)
       (spawn-food))


(defn- eat-food
  [{:keys [food snake grow-by] :as game}]
  (let [[[head-x head-y :as head] & body] snake]
    (if (= food head)
      (-> game
          (assoc :food nil)
          (update :grow (fn [grow] (+ grow grow-by)))
          spawn-food)
      game)))

(defn- spawn-hairball
  [game]
  )

(defn- update-hairballs
  [game]
  game)

(defn move
  "dont make any turns, keep moving in same direction"
  [{:keys [state snake width height hairballs]
    [food-x food-y :as food] :food
    :as game}
   direction]
  (if (= state :playing)
    (let [[[head-x head-y :as head] & body] snake
          [prev-x prev-y :as prev-pos] (first body)
          [nx ny :as new-head] (case direction
                                 :left [(- head-x 1) head-y]
                                 :right [(+ head-x 1) head-y]
                                 :up [head-x (+ head-y 1)]
                                 :down [head-x (- head-y 1)])]
      (if (= prev-pos new-head)
        ;; going backwards not allowed, ignore and move forward
        (move-forward game)

        ;; move
        (if (or (< nx 0)
                (< ny 0)
                (>= nx width)
                (>= ny height)
                (contains? (set snake) new-head)
                (contains? hairballs new-head))
          ;; ran into something, die
          (assoc game :state :dead)

          ;; otherwise
          (-> game
              eat-food
              update-hairballs
              ((fn [{:keys [grow] :as game}]
                 (if (> grow 0)
                   (-> game
                       (assoc :snake (concat [new-head]
                                             snake))
                       (update :grow dec))
                   (assoc game :snake (concat [new-head]
                                              (butlast snake))))))
              (assoc :direction direction)))))
    game))


#_ (-> (setup-game)
       (move :left))
;; => {:width 16, :height 12, :state :playing, :direction :left, :snake ([7 6] [8 6]), :food nil}

(defn move-forward
  [{:keys [direction] :as game}]
  (move game direction))

#_ (-> (setup-game)
       move-forward
       move-forward)
;; => {:width 16, :height 12, :state :playing, :direction :left, :snake ([6 6] [7 6]), :food nil}
