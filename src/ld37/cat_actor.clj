(ns ld37.cat-actor
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.actions Actions
                                                    MoveToAction
                                                    RotateToAction
                                                    ParallelAction))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]))

(defn make-cat-head-actor!
  [game]
  (let [last-head-position (atom nil)
        {:keys []} @game
        head (proxy [Image] [(.get am/manager "images/head.png" Texture)]
               (act [delta]
                 (let [{[[head-x head-y :as head-position] & _] :snake} @game
                       [screen-x screen-y] (c/game-to-screen-pos-centered this head-position)]
                   (cond (nil? @last-head-position)
                         (do (.setPosition this screen-x screen-y)
                             (reset! last-head-position head-position))

                         (not= @last-head-position head-position)
                         (do (.addAction this (Actions/moveTo screen-x screen-y c/game-speed))
                             (reset! last-head-position head-position))

                         ;; do nothing, already animating
                         :else nil))
                 (proxy-super act delta)))]
    head))

(defn make-body-tile!
  [game index]
  (let [straight (am/make-texture-drawable "images/straight.png")
        turn (am/make-texture-drawable "images/turn.png")
        image (proxy [Image] [straight]
                (act [delta]
                  (let [{:keys [snake]} @game
                        body snake
                        [x y :as pos] (nth body index)
                        [prev-x prev-y :as prev-pos] (nth body (dec index))
                        [next-x next-y :as next-pos] (nth body (inc index))]
                    (cond
                      ;; straight north/south
                      (or (and (< prev-y y)
                               (> next-y y))
                          (and (> prev-y y)
                               (< next-y y)))
                      (do
                        (.setDrawable this straight)
                        (.setOrigin this
                                    (/ (.getWidth this) 2)
                                    (/ (.getHeight this) 2))
                        (.setRotation this 0))

                      ;; straight east/west
                      (or (and (< prev-x x)
                               (> next-x x))
                          (and (> prev-x x)
                               (< next-x x)))
                      (do
                        (.setDrawable this straight)
                        (.setOrigin this
                                    (/ (.getWidth this) 2)
                                    (/ (.getHeight this) 2))
                        (.setRotation this 90))

                      ;; turn 1
                      (or (and (< next-x x)
                               (> prev-y y))
                          (and (< prev-x x)
                               (> next-y y)))
                      (do (.setDrawable this turn)
                          (.setOrigin this
                                      (/ (.getWidth this) 2)
                                      (/ (.getHeight this) 2))
                          (.setRotation this (* 3 90)))

                      ;; turn 2
                      (or (and (> next-x x)
                               (> prev-y y))
                          (and (> prev-x x)
                               (> next-y y)))
                      (do (.setDrawable this turn)
                          (.setOrigin this
                                      (/ (.getWidth this) 2)
                                      (/ (.getHeight this) 2))
                          (.setRotation this (* 2 90)))

                      ;; turn 3
                      (or (and (> next-x x)
                               (< prev-y y))
                          (and (> prev-x x)
                               (< next-y y)))
                      (do (.setDrawable this turn)
                          (.setOrigin this
                                      (/ (.getWidth this) 2)
                                      (/ (.getHeight this) 2))
                          (.setRotation this 90))

                      ;; turn 4
                      (or (and (< next-x x)
                               (< prev-y y))
                          (and (< prev-x x)
                               (< next-y y)))
                      (do (.setDrawable this turn)
                          (.setOrigin this
                                      (/ (.getWidth this) 2)
                                      (/ (.getHeight this) 2))
                          (.setRotation this 0)))
                    (c/set-actor-game-position this pos)
                    (proxy-super act delta))))]
    image))

(defn make-body-tiles!
  [game]
  (let [{:keys [snake]} @game
        [head & body] snake]
    (mapv (fn [index]
            (make-body-tile! game index))
          (range 1 (count body)))))

(defn- set-centered-rotation
  [actor d]
  (.setOrigin actor
              (/ c/tile-width 2)
              (/ c/tile-width 2))
  (.setRotation actor d))

(defn make-cat-tail-actor!
  [game]
  (let [left-image (am/make-texture-drawable "images/tail.png")
        right-image (am/make-texture-drawable-flip "images/tail.png" true false)
        tail-actor (proxy [Image] [left-image]
                     (act [delta]
                       (let [{:keys [snake]} @game
                             [tail-x tail-y :as tail-pos] (last snake)
                             [prev-x prev-y :as prev-pos] (last (butlast snake))]
                         (cond
                           (< prev-x tail-x)
                           (do (.setDrawable this left-image)
                               (set-centered-rotation this 0)
                               (c/set-actor-game-position this tail-pos))

                           (> prev-x tail-x)
                           (do (.setDrawable this right-image)
                               (set-centered-rotation this 0)
                               (.setPosition this
                                             (- (* c/tile-width tail-x)
                                                (- (.getWidth this)
                                                   c/tile-width))
                                             (* c/tile-width tail-y)))

                           (> prev-y tail-y)
                           (do (.setDrawable this left-image)
                               (set-centered-rotation this -90)
                               (c/set-actor-game-position this tail-pos))

                           (< prev-y tail-y)
                           (do (.setDrawable this left-image)
                               (set-centered-rotation this 90)
                               (c/set-actor-game-position this tail-pos))))
                       (proxy-super act delta)))]
    tail-actor))

(defn- rotate-and-move-to
  [actor d [x y]]
  (let [current-rotation (.getRotation actor)
        original-rotation d
        rotation-difference (Math/abs (- current-rotation d))
        d (if (> rotation-difference 180)
            (- d 360)
            d)
        action (Actions/parallel (Actions/rotateTo d c/game-speed)
                                 (Actions/moveTo (* c/tile-width x)
                                                 (* c/tile-width y)
                                                 c/game-speed))]
    (.setOrigin actor
                (/ c/tile-width 2)
                (/ c/tile-width 2))
    (.addAction actor action)))


(defn- move-to
  [actor [x y]]
  (.addAction actor
              (Actions/moveTo (* c/tile-width x)
                              (* c/tile-width y)
                              c/game-speed)))

(defn make-paws-actor!
  [game]
  (let [last-position (atom nil)
        {:keys [snake]} @game
        [[pos-x pos-y :as pos] & _] snake
        paws (proxy [Image] [(am/make-texture-drawable "images/paws.png")]
               (act [delta]
                 (let [{:keys [snake]} @game
                       [[head-x head-y :as head-pos]
                        [pos-x pos-y :as pos]
                        [next-x next-y :as next-pos]& _] snake]
                   (cond (nil? @last-position)
                         (reset! last-position pos)

                         (not= @last-position pos)
                         (do (reset! last-position pos)
                             (cond
                               ;; left
                               (and (> next-x pos-x head-x)
                                    (= head-y pos-y next-y))
                               (move-to this pos)

                               ;; right
                               (and (< next-x pos-x head-x)
                                    (= head-y pos-y next-y))
                               (move-to this pos)

                               ;; down
                               (and (< head-y pos-y next-y)
                                    (= head-x pos-x next-x))
                               (move-to this pos)

                               ;; up
                               (and (> head-y pos-y next-y)
                                    (= head-x pos-x next-x))
                               (move-to this pos)

                               ;; up right
                               (and (= head-y pos-y)
                                    (> pos-y next-y)
                                    (> head-x pos-x)
                                    (= pos-x next-x))
                               (rotate-and-move-to this 180 pos)

                               ;; up left
                               (and (= head-y pos-y)
                                    (> pos-y next-y)
                                    (< head-x pos-x)
                                    (= pos-x next-x))
                               (rotate-and-move-to this 360 pos)

                               ;; down left
                               (and (= head-y pos-y)
                                    (< pos-y next-y)
                                    (< head-x pos-x)
                                    (= pos-x next-x))
                               (rotate-and-move-to this 0 pos)

                               ;; down right
                               (and (= head-y pos-y)
                                    (< pos-y next-y)
                                    (> head-x pos-x)
                                    (= pos-x next-x))
                               (rotate-and-move-to this 180 pos)

                               ;; right up
                               (and (= head-x pos-x)
                                    (> pos-x next-x)
                                    (> head-y pos-y)
                                    (= pos-y next-y))
                               (rotate-and-move-to this 270 pos)

                               ;; right down
                               (and (= head-x pos-x)
                                    (> pos-x next-x)
                                    (< head-y pos-y)
                                    (= pos-y next-y))
                               (rotate-and-move-to this 90 pos)

                               ;; left down
                               (and (= head-x pos-x)
                                    (< pos-x next-x)
                                    (< head-y pos-y)
                                    (= pos-y next-y))
                               (rotate-and-move-to this 90 pos)

                               ;; left up
                               (and (= head-x pos-x)
                                    (< pos-x next-x)
                                    (> head-y pos-y)
                                    (= pos-y next-y))
                               (rotate-and-move-to this 270 pos)))))
                 (proxy-super act delta)))]
    (c/set-actor-game-position paws pos)
    paws))

(defn make-cat-actor!
  [game]
  (let [head (make-cat-head-actor! game)
        tail (make-cat-tail-actor! game)
        body-tiles (atom nil)
        paws-actor (make-paws-actor! game)
        cat-actor (proxy [Group] []
                    (act [delta]
                      (let [{[_ & body] :snake} @game]
                        (when-not (= (count @body-tiles) (count body))
                          ;; remove old body tiles
                          (when-not (empty? @body-tiles)
                            (doseq [body-tile @body-tiles]
                              (.remove body-tile))
                            (reset! body-tiles nil))
                          ;; add body tiles
                          (reset! body-tiles (make-body-tiles! game))
                          (doseq [body-tile @body-tiles]
                            (.addActorBefore this paws-actor body-tile))))
                      (proxy-super act delta)))]
    (.addActor cat-actor tail)
    (.addActor cat-actor paws-actor)
    (.addActor cat-actor head)
    cat-actor))
