(ns metabase.api.timeline-event-test
  "Tests for /api/timeline-event endpoints"
  (:require [clojure.test :refer :all]
            [metabase.http-client :as http]
            [metabase.models.collection :refer [Collection]]
            [metabase.models.timeline :refer [Timeline]]
            [metabase.models.timeline-event :refer [TimelineEvent]]
            [metabase.server.middleware.util :as middleware.u]
            [metabase.test :as mt]
            [metabase.util :as u]
            [toucan.db :as db]))

(deftest auth-tests
  (testing "Authentication"
    (is (= (get middleware.u/response-unauthentic :body)
           (http/client :get 401 "/timeline-event")))
    (is (= (get middleware.u/response-unauthentic :body)
           (http/client :get 401 "/timeline-event/1")))))

(deftest get-timeline-event-test
  (testing "GET /api/timeline-event/:id"
    (let [defaults {:creator_id (u/the-id (mt/fetch-user :rasta))}]
      (mt/with-temp* [Collection    [collection {:name "Important Data"}]
                      Timeline      [timeline (merge defaults {:name          "Important Events"
                                                               :collection_id (u/the-id collection)})]
                      TimelineEvent [event (merge defaults {:name         "Very Important Event"
                                                            :timestamp    (java.time.OffsetDateTime/now)
                                                            :timezone     "PST"
                                                            :time_matters false
                                                            :timeline_id  (u/the-id timeline)})]]
        (testing "check that we get the timeline-event with `id`"
          (is (= (->> (mt/user-http-request :rasta :get 200 (str "timeline-event/" (u/the-id event)))
                      :name)
                 "Very Important Event")))))))

(deftest create-timeline-event-test
  (testing "POST /api/timeline-event"
    (let [defaults {:creator_id (u/the-id (mt/fetch-user :rasta))}]
      (mt/with-temp* [Collection    [collection {:name "Important Data"}]
                      Timeline      [timeline (merge defaults {:name          "Important Events"
                                                               :collection_id (u/the-id collection)})]]
        (testing "create a timeline event"
          ;; make an API call to create a timeline
          (mt/user-http-request :rasta :post 200 "timeline-event" {:name         "Rasta Migrates to Florida for the Winter"
                                                                   :timestamp    (java.time.OffsetDateTime/now)
                                                                   :timezone     "PST"
                                                                   :time_matters false
                                                                   :timeline_id  (u/the-id timeline)}))
          ;; check the Timeline to see if the event is there
          (is (= (-> (db/select-one TimelineEvent :timeline_id (u/the-id timeline)) :name)
                 "Rasta Migrates to Florida for the Winter"))))))

(deftest update-timeline-event-test
  (testing "PUT /api/timeline-event/:id"
    (let [defaults {:creator_id (u/the-id (mt/fetch-user :rasta))}]
      (mt/with-temp* [Collection    [collection {:name "Important Data"}]
                      Timeline      [timeline (merge defaults {:name          "Important Events"
                                                               :collection_id (u/the-id collection)})]
                      TimelineEvent [event (merge defaults {:name         "Very Important Event"
                                                            :timestamp    (java.time.OffsetDateTime/now)
                                                            :timezone     "PST"
                                                            :time_matters false
                                                            :timeline_id  (u/the-id timeline)})]]
        (testing "check that we get the timeline-event with `id`"
          (is (= (->> (mt/user-http-request :rasta :put 200 (str "timeline-event/" (u/the-id event)) {:archived true})
                      :archived)
                 true)))))))